/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"
#include "MarshallInternal.h"

_TPMCPP_BEGIN

//
// MarshallIn.cpp
// 
// Routines for translating a TPM-formatted byte-vector into a TPM structure. Typically called
// from struct->FromBuf(vector<BYTE> bufContainingTpmResponse).
// 
// Marhalling in is easier than marshalling out because we can do it in one pass
// (to marshall-out you have to look-ahead to figure out array lengths, etc.)
// 
// The only complexity here is that unions must be created (new'd) based on the contents
// of a union selector earlier in the structure. Similar for array lengths: they are typically
// in the element immediately before the array they refer to.
//

void TpmStructureBase::FromBufInternal(InByteBuf& buf)
{
    TpmTypeId myId = GetTypeId();

    // The standard marshaller can't handle a few structs like SYM_DEF_OBJECT
    bool processed = FromBufSpecial(buf, myId);

    if (processed) {
        return;
    }
    
    int arrayCountX; // Used in calls to ElementInfo when we don't care about the array size
    TpmStructureBase *pUnion;
    StructMarshallInfo *myInfo = TheTypeMap.GetStructMarshallInfo(myId);

    if (myInfo == NULL) {
        throw domain_error("Not a marshallable type");
    }

    int mshlStartPos = buf.GetPos();
    int curStructSize = 0;

    // Else go through the fields extracting bytes from the InByteBuf, endian-converting
    // them and stuffing them in the structure.
    for (UINT32 j = 0; j < myInfo->Fields.size(); j++) {
        MarshallInfo& fInfo = myInfo->Fields[j];

        if (fInfo.IsArray) {
            // Get the array len (might be len-prepended, fixed, or TPM_ALG_ID-derived
            UINT32 arrayCount = fInfo.ElementMarshallType == MarshallType::EncryptedVariableLengthArray
                              ? buf.sizedStructLen.top() - (buf.GetPos() - mshlStartPos)
                              : GetArrayLen(*myInfo, fInfo);

            // Set the array size
            this->ElementInfo(j, -1, arrayCountX, pUnion, arrayCount);

            // Now all of the array elements.  We only need support structures and value-types
            for (UINT32 count = 0; count < arrayCount; count++) {
                void *pElem = this->ElementInfo(j, count, arrayCountX, pUnion, arrayCount);

                if (fInfo.Sort == ElementSort::TpmStruct) {
                    // fieldPtr set if it's a struct-array member
                    void *fieldPtr = pElem;

                    if (pUnion != NULL) {
                        fieldPtr = (BYTE *)pUnion;
                    }

                    TpmStructureBase *pStruct = (TpmStructureBase *)fieldPtr;
                    // Descend recursively
                    pStruct->FromBufInternal(buf);
                    continue;
                }

                // Else it's a simple value type
                _ASSERT(fInfo.Sort != ElementSort::TpmUnion);
                vector<BYTE> v = buf.GetEndianConvertedVec(fInfo.ElementSize);
                memcpy(pElem, &v[0], fInfo.ElementSize);
                continue;
            }

            // Go to the next field in structure
            continue;
        }

        // Else not an array. Find out where it is, what it is, and then unmarshall it.
        void *pElem = this->ElementInfo(j, -1, arrayCountX, pUnion, -1);

        if (fInfo.Sort == ElementSort::TpmStruct) {
            // For structures we simply descend into the struct.
            TpmStructureBase *s = (TpmStructureBase *)pElem;

            if (pUnion != NULL) {
                s = dynamic_cast<TpmStructureBase *> (pUnion);
            }

            if (curStructSize)
                buf.sizedStructLen.push(curStructSize);
            s->FromBufInternal(buf);
            if (curStructSize) {
                buf.sizedStructLen.pop();
                curStructSize = 0;
            }
            continue;
        }

        if (fInfo.Sort == ElementSort::TpmUnion) {
            // If it is a union, we have to look back to see what the union selector is
            MarshallInfo& unionSelector = myInfo->Fields[fInfo.AssociatedElement];

            void *pSelector = (BYTE *)this->ElementInfo(fInfo.AssociatedElement, -1,
                                                        arrayCountX, pUnion, -1);

            UINT32 selectorVal = GetValFromBuf((BYTE *)pSelector, unionSelector.ElementSize);

            TpmTypeId typeOfUnion = TheTypeMap.GetStructTypeIdFromUnionSelector(fInfo.ThisElementType,
                                                                                selectorVal);
            _ASSERT(typeOfUnion != TpmTypeId::None);

            // Then we have to make a new object of type specified by the selector
            void *pUnion2;
            TpmStructureBase *newObj;
            newObj = TpmStructureBase::Factory(typeOfUnion, fInfo.ThisElementType, pUnion2);

            _ASSERT(pUnion2 != NULL);

            // Copy the pointer to the new object into out struct
            UINT32 Zero = 0;
            _ASSERT(memcmp(pElem, &Zero, sizeof(Zero)) == 0);
            memcpy(pElem, &pUnion2, sizeof(pUnion2));

            // And then get the actual contents of the union
            newObj->FromBufInternal(buf);
            continue;
        }

        if (fInfo.Sort == ElementSort::TpmBitfield ||
            fInfo.Sort == ElementSort::TpmEnum ||
            fInfo.Sort == ElementSort::TpmValueType) {

            // Simple value types are endian-converted into the struct
            void *pField = ElementInfo(j, -1, arrayCountX, pUnion, -1);
            vector<BYTE> v = buf.GetEndianConvertedVec(fInfo.ElementSize);
            memcpy(pField, &v[0], fInfo.ElementSize);
            if (fInfo.ElementMarshallType == MarshallType::LengthOfStruct)
                curStructSize = fInfo.ElementSize == 2 ? *static_cast<unsigned short *>(pField) : *static_cast<int*>(pField);
            continue;
        }

        // Did we forget anything?
        _ASSERT(FALSE);
    }

    return;
}

UINT32 TpmStructureBase::GetArrayLen(StructMarshallInfo& fields, 
                                     MarshallInfo& fieldInfo)
{
    _ASSERT(fieldInfo.IsArray);
    _ASSERT(fieldInfo.ElementMarshallType != MarshallType::FixedLengthArray);

    int arrayCountAtStart;
    TpmStructureBase *yy;
    MarshallInfo& arrayCount = fields.Fields[fieldInfo.AssociatedElement];
    void *pLength = this->ElementInfo(fieldInfo.AssociatedElement, -1, arrayCountAtStart, yy, -1);
    int countSize = arrayCount.ElementSize;
    UINT32 count = GetValFromBuf((BYTE *)pLength, countSize);

    if (fieldInfo.ElementMarshallType == MarshallType::VariableLengthArray) {
        return count;
    }

    if (fieldInfo.ElementMarshallType == MarshallType::SpecialVariableLengthArray) {
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
                throw domain_error("not implemented");
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
        TPMT_SYM_DEF_OBJECT *sdo = dynamic_cast<TPMT_SYM_DEF_OBJECT *>(this);
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
        TPMT_SYM_DEF *sdo = dynamic_cast<TPMT_SYM_DEF *>(this);
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