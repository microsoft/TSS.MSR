/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"

//#include <new>

_TPMCPP_BEGIN

/// <summary>Marshals the TPM structure to the provided OutByteBuf.</summary>
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

    const TpmTypeId myId = GetTypeId();
    std::vector<MarshalInfo>& fields = TypeMap[myId]->Fields;

    // Size-of-struct fields are filled in once the size is actually known. This is always
    // when processing the next element. The following state tracks this so that we can
    // fill that information in once known.
    bool processSizeOfStruct = false, processSizeOfStructNext = false;
    int sizeBufPos = -1;

    TpmStructureBase *ncThis = const_cast<TpmStructureBase*>(this);

    // Iterate through the fields marshalling them out to the byte-array and string array.
    for (unsigned int j = 0; j < fields.size(); j++)
    {
        processSizeOfStruct = processSizeOfStructNext;
        
        // Marshaling info for this field
        MarshalInfo& field = fields[j];
        TpmTypeInfo& fieldInfo = *TypeMap[field.TypeId];

        int arrayCount;

        // TPM structures contain (1) value elements, (2) structures, (3) "unions" (one of the
        // objects derived from a particular TPMU_ case class, and (4) arrays of structs or
        // value types. The first two are simple members, the third is a pointer to a dynamically-
        // allocated object derived from the union base.

        // ElementInfo returns pointers to the start of the element in the structure, and (for
        // unions) returns the pointer to the union object itself cast to a TpmStructureBase. 
        // If the object is an array, the array size is also returned.
        TpmStructureBase *pStruct;
        void* pElem = (BYTE*)ncThis->ElementInfo(j, -1, arrayCount, pStruct, -1);

        // Process selector and length fields. These need us to "look ahead" in the structure
        // (array-count or union selector) or "fill them in later" (lengthOfStruct).
        switch (field.MarshalType)
        {
            case MarshalType::UnionSelector: {
                // We set the value of the union selector based on the union object somewhere
                // later in the struct.
                MarshalInfo& unionElem = fields[field.AssociatedField];
                TpmStructureBase *pUnionObject;
                int unusedArraySize;
                ncThis->ElementInfo(field.AssociatedField, -1, unusedArraySize, pUnionObject, -1);

                _ASSERT(pUnionObject != NULL);

                // Get the type of the union object
                TpmTypeId selectorTypeId = pUnionObject->GetTypeId();

                // Look up the associated selector value
                UINT32 selectorVal = TypeMap[unionElem.TypeId]->GetUnionSelectorFromTypId(selectorTypeId);
                // And set the selector appropriately
                CopyUint(pElem, selectorVal, fieldInfo.Size);
                break;
            }

            case MarshalType::ArrayCount:
            {
                // We must set the array cont from the size of the array that follows the
                // arrayCount element.
                MarshalInfo& nextField = fields[j + 1];

                _ASSERT(nextField.IsArray());
                
                // Get the array size
                int arrayCount;
                TpmStructureBase *yy;
                ncThis->ElementInfo(j + 1, -1, arrayCount, yy, -1);

                // And set the array count in our structure
                sizeBufPos = outBuf.GetPos();
                CopyUint(pElem, arrayCount, fieldInfo.Size);
                break;
            }

            case MarshalType::LengthOfStruct:
            {
                // LengthOfStruct is the length of the field that follows this one.
                // There are several ways we can do this:  Here we keep breadcrumbs so that we
                // can fill in the size after the next element is marshalled.
                UINT16 tempSize = 0xFFFF;
                _ASSERT(fieldInfo.Size == 2); // TPM2B
                CopyUint(pElem, tempSize, 2);
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

        if (field.IsArray())
        {
            int arrayCount, xx;
            TpmStructureBase *pUnion;

            ncThis->ElementInfo(j, -1, arrayCount, pUnion, -1);

            for (int k = 0; k < arrayCount; k++) {
                void *pElem = ncThis->ElementInfo(j, k, xx, pUnion, -1);

                if (pUnion != NULL) {
                    pElem = pUnion;
                }

                // Tthere are no arrays of unions.
                _ASSERT(fieldInfo.Kind != TpmTypeKind::TpmUnion);

                if (fieldInfo.Kind == TpmTypeKind::TpmStruct)
                    ((TpmStructureBase *)pElem)->MarshallInternal(outBuf);
                else
                    outBuf << ToNet((BYTE*)pElem, fieldInfo.Size);
            }
        } else {
            // We don't have an array: it must be a struct, a union, or a value-type.
            switch (fieldInfo.Kind) {
                case TpmTypeKind::TpmStruct: {
                    TpmStructureBase *s = (TpmStructureBase*)pElem;

                    _ASSERT(pStruct);
                    if (pStruct != NULL)
                        s = pStruct;

                    s->MarshallInternal(outBuf);
                    break;
                }

                case TpmTypeKind::TpmUnion: {
                    TpmStructureBase *s = dynamic_cast<TpmStructureBase*>(pStruct);
                    s->MarshallInternal(outBuf);
                    break;
                }

                default: {
                    // Everything else is value-types
                    outBuf << ToNet(pElem, fieldInfo.Size);
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

    if (myId == TpmTypeId::TPMT_SYM_DEF_OBJECT_ID)
    {
        const TPMT_SYM_DEF_OBJECT *sdo = dynamic_cast<const TPMT_SYM_DEF_OBJECT*>(this);

        if (sdo->algorithm == TPM_ALG_ID::_NULL) {
            // Just output the NULL alg and stop. This might not work for XOR.
            outBuf << sdo->algorithm;
            return true;
        }

        outBuf << sdo->algorithm << sdo->keyBits << sdo->mode;
        return true;
    }

    if (myId == TpmTypeId::TPMT_SYM_DEF_ID)
    {
        const TPMT_SYM_DEF *sd = dynamic_cast<const TPMT_SYM_DEF*>(this);

        if (sd->algorithm == TPM_ALG_ID::_NULL) {
            // Just output the NULL alg and stop. This might not work for XOR
            outBuf << (UINT16)sd->algorithm;
            return true;
        }

        outBuf << sd->algorithm << sd->keyBits << sd->mode;
        return true;
    }

    return false;
}

/// <summary>Translates (unmarshals) TPM-formatted byte buffer into a TPM structure.
/// 
/// Unmarhalling is easier than marshaling out because it can be done in one pass
/// (to marshal-out a look-ahead is required to figure out array lengths, etc.)
/// 
/// The only complexity here is that unions must be created (new'd) based on the contents
/// of a union selector earlier in the structure. Similar for array lengths: they are
/// typically in the element immediately before the array they refer to.</summary>
void TpmStructureBase::FromBufInternal(InByteBuf& buf)
{
    TpmTypeId myId = GetTypeId();

    // The standard marshaller can't handle a few structs like SYM_DEF_OBJECT
    bool processed = FromBufSpecial(buf, myId);
    if (processed)
        return;
    
    int arrayCountX; // Used in calls to ElementInfo when we don't care about the array size
    TpmStructureBase *pStruct = NULL;
    TpmTypeInfo& myInfo = *TypeMap[myId];

    int mshlStartPos = buf.GetPos();
    int curStructSize = 0;

    // Else go through the fields extracting bytes from the InByteBuf, endian-converting
    // them and stuffing them in the structure.
    for (UINT32 j = 0; j < myInfo.Fields.size(); j++)
    {
        MarshalInfo& field = myInfo.Fields[j];
        TpmTypeInfo& fieldInfo = *TypeMap[field.TypeId];

        if (field.IsArray()) {
            // Get the array len (might be len-prepended, fixed, or TPM_ALG_ID-derived
            UINT32 arrayCount = field.MarshalType == MarshalType::EncryptedVariableLengthArray
                              ? buf.sizedStructLen.top() - (buf.GetPos() - mshlStartPos)
                              : GetArrayLen(myInfo, field);

            // Set the array size
            this->ElementInfo(j, -1, arrayCountX, pStruct, arrayCount);

            // Now all of the array elements.  We only need support structures and value-types
            for (UINT32 count = 0; count < arrayCount; count++)
            {
                void *pElem = this->ElementInfo(j, count, arrayCountX, pStruct, arrayCount);

                if (fieldInfo.Kind == TpmTypeKind::TpmStruct) {
                    _ASSERT(pStruct);
                    if (!pStruct)
                        pStruct = (TpmStructureBase*)pElem;
                    // Descend recursively
                    pStruct->FromBufInternal(buf);
                    continue;
                }

                // Else it's a simple value type
                _ASSERT(fieldInfo.Kind != TpmTypeKind::TpmUnion);
                ByteVec v = buf.GetEndianConvertedVec(fieldInfo.Size);
                memcpy(pElem, &v[0], fieldInfo.Size);
                continue;
            }

            // Go to the next field in structure
            continue;
        }

        // Else not an array. Find out where it is, what it is, and then unmarshall it.
        void *pElem = this->ElementInfo(j, -1, arrayCountX, pStruct, -1);

        if (fieldInfo.Kind == TpmTypeKind::TpmStruct)
        {
            // For structures we simply descend into the struct.
            TpmStructureBase *s = (TpmStructureBase *)pElem;

            _ASSERT(pStruct);
            if (pStruct != NULL)
                s = dynamic_cast<TpmStructureBase*>(pStruct);

            if (curStructSize)
                buf.sizedStructLen.push(curStructSize);
            s->FromBufInternal(buf);
            if (curStructSize) {
                buf.sizedStructLen.pop();
                curStructSize = 0;
            }
            continue;
        }
        else if (fieldInfo.Kind == TpmTypeKind::TpmUnion)
        {
            // If it is a union, we have to look back to see what the union selector is
            MarshalInfo& unionSelector = myInfo.Fields[field.AssociatedField];

            void *pSelector = ElementInfo(field.AssociatedField, -1,arrayCountX, pStruct, -1);

            UINT32 selectorVal = GetValFromBuf((BYTE*)pSelector, TypeMap[unionSelector.TypeId]->Size);

            TpmTypeId typeOfUnion = TypeMap[field.TypeId]->GetStructTypeIdFromUnionSelector(selectorVal);
            _ASSERT(typeOfUnion != TpmTypeId::None);

            // Then we have to make a new object of type specified by the selector,
            // wrap it into a smart pointer, and store the smart pointer in the corersponding field location
            TpmStructureBase *newObj = TpmStructureBase::UnionFactory(typeOfUnion, field.TypeId, pElem);

            // And then unmarshal the actual contents of the union member
            newObj->FromBufInternal(buf);
            continue;
        }
        else {
            // Simple value types are endian-converted into the struct
            void *pField = ElementInfo(j, -1, arrayCountX, pStruct, -1);
            ByteVec v = buf.GetEndianConvertedVec(fieldInfo.Size);
            memcpy(pField, &v[0], fieldInfo.Size);
            if (field.MarshalType == MarshalType::LengthOfStruct)
            {
                curStructSize = fieldInfo.Size == 2 ? *static_cast<unsigned short*>(pField)
                                                    : *static_cast<int*>(pField);
            }
            continue;
        }

        // Did we forget anything?
        _ASSERT(FALSE);
    }

    return;
}

UINT32 TpmStructureBase::GetArrayLen(TpmTypeInfo& fields, MarshalInfo& fieldInfo)
{
    _ASSERT(fieldInfo.IsArray());

    int arrayCountAtStart;
    TpmStructureBase *yy;
    MarshalInfo& arrayCount = fields.Fields[fieldInfo.AssociatedField];
    void *pLength = this->ElementInfo(fieldInfo.AssociatedField, -1, arrayCountAtStart, yy, -1);
    int countSize = TypeMap[arrayCount.TypeId]->Size;
    UINT32 count = GetValFromBuf((BYTE *)pLength, countSize);

    if (fieldInfo.MarshalType == MarshalType::VariableLengthArray) {
        return count;
    }

    if (fieldInfo.MarshalType == MarshalType::SpecialVariableLengthArray) {
        // Special handling for digests
        TPM_ALG_ID algId = (TPM_ALG_ID)count;
        int numBytes;

        switch (algId) {
            case TPM_ALG_ID::SHA1:
                numBytes = 20;
                break;

            case TPM_ALG_ID::SHA256:
                numBytes = 32;
                break;

            case TPM_ALG_ID::SHA384:
                numBytes = 48;
                break;

            case TPM_ALG_ID::SHA512:
                numBytes = 64;
                break;

            case TPM_ALG_ID::SM3_256:
                numBytes = 32;
                break;

            default:
                throw std::domain_error("not implemented");
        }

        count = numBytes;

        return count;
    }

    _ASSERT(FALSE);
    return -1;
}

bool TpmStructureBase::FromBufSpecial(InByteBuf& buf, TpmTypeId tp)
{
    if (tp == TpmTypeId::TPMT_SYM_DEF_OBJECT_ID) {
        TPMT_SYM_DEF_OBJECT *sdo = dynamic_cast<TPMT_SYM_DEF_OBJECT*>(this);
        UINT16 x;
        buf >> x;
        sdo->algorithm = (TPM_ALG_ID)x;

        if (sdo->algorithm == TPM_ALG_ID::_NULL) {
            return true;
        }

        buf >> sdo->keyBits;
        buf >> x;
        sdo->mode = (TPM_ALG_ID)x;
        return true;
    }

    if (tp == TpmTypeId::TPMT_SYM_DEF_ID) {
        TPMT_SYM_DEF *sdo = dynamic_cast<TPMT_SYM_DEF*>(this);
        UINT16 x;
        buf >> x;
        sdo->algorithm = (TPM_ALG_ID)x;

        if (sdo->algorithm == TPM_ALG_ID::_NULL) {
            return true;
        }

        buf >> sdo->keyBits;
        buf >> x;
        sdo->mode = (TPM_ALG_ID)x;
        return true;
    }

    return false;
}

_TPMCPP_END