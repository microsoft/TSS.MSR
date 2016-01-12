/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"
#include "MarshallInternal.h"

using namespace std;

_TPMCPP_BEGIN

/// <summary>Serialize this structure to the provided OutByteBuf. This fucntion will typically
/// descend recursively into any contained fucntions</summary>
void TpmStructureBase::MarshallInternal(OutByteBuf& outBuf) const
{
    //
    // The marshaller iterates through all the fields in a structure convcerting them to 
    // TPM-representation in the OutByteBuf.
    //
    // TSS.c++ "infers" some structure parameters so that the programmer doesn't have to
    // explicitly provide them. The first example is array-lengths: since TSS.c++ uses std:vector
    // for arrays, we can fill this in for the user. The second example is the type selector for
    // a union. TSS.C++ uses classes derived from a TPMU_ base class for unions. THe type selector
    // is determined by the type of the actual object that the user provided.
    //
    // Because of this, marshalling occurs in two phases. If the field is not yet known (because
    // it's an array-len, or union type-selector) we "look ahead" to the related field and set
    // the value.
    //
    // Once this is done (or if it is not done if it was not needed) we can then marshall
    // the element.
    //
    // Finally, there are a few places where the spec uses a length field for the marshalled size
    // of the structure that follows it. In these cases we go back and fill on the length once
    // it is known.
    //

    // The TPM spec has some optional parameters that are indicated by a zero-length-field.
    // If *this is such an element do nothing (the zero-length field will be calcluated in the
    // outer structure
    if (this->NullElement()) {
        return;
    }

    // The standard marshaller can't handle everything: if the function below says it's handled
    // the marshaling do nothing
    bool nonStandardMarshal = NonDefaultMarshall(outBuf);
    if (nonStandardMarshal) {
        return;
    }

    // Get my marshaling info
    const TpmTypeId myId = GetTypeId();
    StructMarshallInfo *myTypeInfo = TypeMap[myId];

    _ASSERT(myTypeInfo != NULL);

    // These are my fields
    std::vector<MarshallInfo>& fields = myTypeInfo->Fields;

    // Size-of-struct fields are filled in once the size is actually known. This is always
    // when processing the next element. The following state tracks this so that we can
    // fill that information in once known.
    bool processSizeOfStruct = false, processSizeOfStructNext = false;
    int sizeBufPos = -1;

    TpmStructureBase *ncMe = const_cast<TpmStructureBase *>(this);

    // Used when we don't need the array length in FieldInfo
    int _unused;

    // Iterate through the fields marshalling them out to the byte-array and string array.
    for (unsigned int j = 0; j < fields.size(); j++) {
        processSizeOfStruct = processSizeOfStructNext;
        
        // Marshaling info for this field
        MarshallInfo& field = fields[j];
        int arrayCount;

        // TPM structures contain (1) value elements, (2) structures, (3) "unions" (one of the
        // objects derived from a particular TPMU_ case class, and (4) arrays of structs or
        // value types. The first two are simple members, the third is a pointer to a dynamically-
        // allocated object derived from the union base.

        // ElementInfo returns pointers to the start of the element in the structure, and (for
        // unions) returns the pointer to the union object itself cast to a TpmStructureBase. 
        // If the object is an array, the array size is also returned.
        TpmStructureBase *pUnion;
        BYTE *elementStart = (BYTE *) ncMe->ElementInfo(j, -1, arrayCount, pUnion, -1);

        // Process selector and length fields. These need us to "look ahead" in the structure
        // (array-count or union selector) or "fill them in later" (lengthOfStruct).
        switch (field.ElementMarshallType) {
            case MarshallType::UnionSelector: {
                // We set the value of the union selector based on the union object somewhere
                // later in the struct.
                MarshallInfo& unionElem = fields[field.AssociatedElement];
                TpmStructureBase *pUnionObject;
                ncMe->ElementInfo(field.AssociatedElement, -1, _unused, pUnionObject, -1);

                _ASSERT(pUnionObject != NULL);

                // Get the type of the union object
                TpmTypeId selectorTypeId = pUnionObject->GetTypeId();

                // Look up the associated selector value
                UINT32 selectorVal = TheTypeMap.GetUnionSelectorFromTypId(unionElem.ThisElementType,
                                                                          selectorTypeId);
                // And set the selector appropriately
                CopyUint(elementStart, selectorVal, field.ElementSize);
                break;
            }

            case MarshallType::ArrayCount: {
                // We must set the array cont from the size of the array that follows the
                // arrayCount element.
                MarshallInfo& nextField = fields[j + 1];

                _ASSERT(nextField.IsArray ||
                        nextField.ElementMarshallType == MarshallType::SpecialVariableLengthArray);
                
                // Get the array size
                int arrayCount;
                TpmStructureBase *yy;
                ncMe->ElementInfo(j + 1, -1, arrayCount, yy, -1);

                // And set the array count in our structure
                sizeBufPos = outBuf.GetPos();
                CopyUint(elementStart, arrayCount, field.ElementSize);
                break;
            }

            case MarshallType::LengthOfStruct: {
                // LengthOfStruct is the length of the field that follows this one.
                // There are several ways we can do this:  Here we keep breadcrumbs so that we
                // can fill in the size after the next element is marshalled.
                UINT16 tempSize = 0xFFFF;
                _ASSERT(field.ElementSize == 2); //TPM2B
                CopyUint(elementStart, tempSize, 2);
                processSizeOfStructNext = true;
                sizeBufPos = outBuf.GetPos();
                break;

            }

            default:;
                // The other marshall-types do not need special processing
        }

        // Actually marshall the field. We need to handle two cases: arrays-of-objects and simple.
        UINT32 numBytes = 0;
        int startPos = outBuf.GetPos();

        if (field.IsArray) {
            int arrayCount, xx;
            TpmStructureBase *pUnion;

            ncMe->ElementInfo(j, -1, arrayCount, pUnion, -1);

            for (int k = 0; k < arrayCount; k++) {
                void *pElem = ncMe->ElementInfo(j, k, xx, pUnion, -1);

                if (pUnion != NULL) {
                    pElem = pUnion;
                }

                _ASSERT(field.Sort != ElementSort::TpmUnion);

                if (field.Sort == ElementSort::TpmStruct) {
                    ((TpmStructureBase *)pElem)->MarshallInternal(outBuf);
                } else {
                    // Else must be a value-type (there are no arrays of unions).
                    int elemSize = field.ElementSize;
                    outBuf << ToNet((BYTE *)pElem, elemSize);
                }
            }
        } else {
            // We don't have an array: it must be a struct, a union, or a value-type.
            switch (field.Sort) {
                case ElementSort::TpmStruct: {
                    TpmStructureBase *s = (TpmStructureBase *)elementStart;

                    if (pUnion != NULL) {
                        s = pUnion;
                    }

                    s->MarshallInternal(outBuf);
                    break;

                }

                case ElementSort::TpmUnion: {
                    TpmStructureBase *s = dynamic_cast<TpmStructureBase *>(pUnion);
                    s->MarshallInternal(outBuf);
                    break;

                }

                default: {
                    // Everything else is value-types
                    int elemSize = field.ElementSize;
                    outBuf << ToNet(elementStart, elemSize);
                    break;
                }
            }
        } // End of is-not-an-array

        // How big was the last field we marshalled?
        int endPos = outBuf.GetPos();
        numBytes = endPos - startPos;

        // Update sizeOfBuf (if needed)
        if (processSizeOfStruct) {
            processSizeOfStructNext = false;
            _ASSERT(sizeBufPos >= 0);

            // Earlier assert will fire if this is not a ushort
            UINT16 netSize = htons(numBytes);
            memcpy(outBuf.GetBufPtr(sizeBufPos), (BYTE *)&netSize, 2);
        }

    } // End of j-field loop

    // And, once we've done all the fields, we're done.
    return;
}

bool TpmStructureBase::NonDefaultMarshall(OutByteBuf& outBuf) const
{
    TpmTypeId myId = this->GetTypeId();

    if (myId == TpmTypeId::TPMT_SYM_DEF_OBJECT_ID) {
        const TPMT_SYM_DEF_OBJECT *sdo = dynamic_cast<const TPMT_SYM_DEF_OBJECT *>(this);

        if (sdo->algorithm == TPM_ALG_ID::_NULL) {
            // Just output the NULL alg and stop. This might not work for XOR.
            outBuf << ToIntegral(sdo->algorithm);
            return true;
        }

        outBuf << ToIntegral(sdo->algorithm) << sdo->keyBits << ToIntegral(sdo->mode);
        return true;
    }

    if (myId == TpmTypeId::TPMT_SYM_DEF_ID) {
        const TPMT_SYM_DEF *sd = dynamic_cast<const TPMT_SYM_DEF *>(this);

        if (sd->algorithm == TPM_ALG_ID::_NULL) {
            // Just output the NULL alg and stop. This might not work for XOR
            outBuf << (UINT16)sd->algorithm;
            return true;
        }

        outBuf << ToIntegral(sd->algorithm) << sd->keyBits << ToIntegral(sd->mode);
        return true;
    }

    return false;
}

_TPMCPP_END