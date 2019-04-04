/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "MarshallInternal.h"
#include "Tpm2.h"

_TPMCPP_BEGIN

#define QUOTE "\""

string OutStructSerializer::Serialize(TpmStructureBase *p)
{
    if (tp == SerializationType::None) {
        return "";
    }

    if (!((tp == SerializationType::Text) || (tp == SerializationType::JSON))) {
        throw runtime_error("Not implemented");
    }

    int xx;
    TpmStructureBase *yy;
    const TpmTypeId tpId = p->GetTypeId();
    StructMarshallInfo *tpInfo = TypeMap[tpId];
    std::vector<MarshallInfo>& fields = tpInfo->Fields;

    StartStruct(tpInfo->Name);

    for (int j = 0; j < (int)fields.size(); j++) {
        bool lastInStruct = j == (int)fields.size() - 1;
        void *fieldPtr = p->ElementInfo(j, -1, xx, yy, -1);

        if (yy != NULL) {
            fieldPtr = yy;
        }

        MarshallInfo& fInfo = fields[j];
        OutTypeAndName(fInfo.ThisElementTypeName, fInfo.ElementName, fInfo.IsArray);

        if (fInfo.IsArray) {
            // Special handling for byte-arrays
            if (fInfo.ThisElementType == TpmTypeId::BYTE_ID) {
                vector<BYTE> *vec = static_cast<vector<BYTE>*> (fieldPtr);
                OutByteArray(*vec, lastInStruct);
                continue;
            }

            // Else interate
            int arrayCount;

            p->ElementInfo(j, -1, arrayCount, yy, -1);
            StartArray(arrayCount);

            for (int c = 0; c < arrayCount; c++) {
                void *pElem = p->ElementInfo(j, c, xx, yy, -1);

                if (yy != NULL) {
                    pElem = yy;
                }

                Indent();
                bool lastElem = c == arrayCount - 1;
                OutValue(fInfo, pElem, lastElem);
            }

            EndArray();
            if (!lastInStruct) {
                OutArrayElementSeparator();
            }

            continue;
        }

        // Else not an array
        OutValue(fInfo, fieldPtr, lastInStruct);
    }

    EndStruct(tpInfo->Name);
    return s.str();
}

void OutStructSerializer::OutTypeAndName(string elementType, string elementName, BOOL isArray)
{
    switch (tp) {
        case SerializationType::Text: {
            string isArrayTag = isArray ? "[]" : "";
            s << spaces(indent) << elementType << isArrayTag << " " << elementName << " = ";
            return;
        }

        case SerializationType::JSON: {
            s << spaces(indent) << QUOTE << elementName << QUOTE << ":";
            return;
        }

        default:
            _ASSERT(FALSE);
    }
}

void OutStructSerializer::OutArrayElementSeparator()
{
    switch (tp) {
        case SerializationType::Text: {
            return;
        }

        case SerializationType::JSON: {
            s << ",";
            return;
        }

        default:
            _ASSERT(FALSE);
    }
}

void OutStructSerializer::Indent()
{
    switch (tp) {
        case SerializationType::Text:
        case SerializationType::JSON: {
            s << spaces(indent);
            return;
        }

        default:
            _ASSERT(FALSE);
    }
}

void OutStructSerializer::OutValue(MarshallInfo& fInfo, void *pElem, bool lastInStruct)
{
    switch (tp) {
        case SerializationType::Text: {
            if (fInfo.Sort == ElementSort::TpmStruct) {
                TpmStructureBase *b = static_cast<TpmStructureBase *> (pElem);
                Serialize(b);
                s << endl;
                return;
            }

            if (fInfo.Sort == ElementSort::TpmUnion) {
                TpmStructureBase *elem = (TpmStructureBase *)(pElem);
                Serialize(elem);
                s << endl;
                return;
            }

            if ((fInfo.Sort == ElementSort::TpmBitfield) || 
                (fInfo.Sort == ElementSort::TpmEnum) ||
                (fInfo.Sort == ElementSort::TpmValueType)) {
                int len = fInfo.ElementSize;
                UINT64 val = GetValFromByteBuf((BYTE *)pElem, fInfo.ElementSize);
                StructMarshallInfo *fieldInfo = TheTypeMap.GetStructMarshallInfo(fInfo.ThisElementType);

                if (fieldInfo == NULL) {
                    // Numeric
                    s << "0x" << setfill('0') << setw(len) << hex << val << 
                         " (" << dec << val << ")" << setw(1) << endl;
                }
                else {
                    // Convert enum to string
                    string enumString = GetEnumString((UINT32)val, *fieldInfo);
                    int col = GetColumn(s);
                    string lineFeed = "";

                    if (enumString.find('|') != string::npos) {
                        enumString = AlignToColumns(enumString, '|', col);
                        lineFeed = "\n" + string(col, ' ');
                    }

                    s << enumString << lineFeed << " (0x" << hex << val << ")" << endl;
                }

                return;
            }

            _ASSERT(FALSE);
        }

        case SerializationType::JSON: {
            if (fInfo.Sort == ElementSort::TpmStruct) {
                TpmStructureBase *b = static_cast<TpmStructureBase *> (pElem);
                Serialize(b);

                if (!lastInStruct) {
                    s << " , ";
                }

                s << endl;
                return;
            }

            if (fInfo.Sort == ElementSort::TpmUnion) {
                TpmStructureBase *elem = (TpmStructureBase *)(pElem);
                Serialize(elem);

                if (!lastInStruct) {
                    s << " , ";
                }

                s << endl;
                return;
            }

            if ((fInfo.Sort == ElementSort::TpmBitfield) || 
                (fInfo.Sort == ElementSort::TpmEnum) ||
                (fInfo.Sort == ElementSort::TpmValueType)) {
                UINT64 val = GetValFromByteBuf((BYTE *)pElem, fInfo.ElementSize);
                s << val;

                if (!lastInStruct) {
                    s << " , ";
                }

                s << endl;
                return;
            }

            _ASSERT(FALSE);
        }

        default:
            _ASSERT(FALSE);
    }
}

void OutStructSerializer::OutByteArray(vector<BYTE>& arr, bool lastInStruct)
{
    switch (tp) {
        case SerializationType::Text: {
            size_t maxSize = 17;
            size_t size = arr.size();

            if (precise) {
                maxSize = size + 1;
            }

            if (size > maxSize) {
                size = maxSize;
            }

            s << "[";

            for (size_t j = 0; j < arr.size(); j++) {
                if ((j > maxSize) && (j < arr.size() - 4)) {
                    continue;
                }

                s << hex << setfill('0') << setw(2) << (UINT32)arr[j];

                if ((j % 4 == 3) && (j != arr.size() - 1)) {
                    s << " ";
                }

                if (j == maxSize) {
                    s << "...  ";
                }
            }

            s << "]" << endl;
            return;
        }

        case SerializationType::JSON: {
            s << "[";

            for (size_t j = 0; j < arr.size(); j++) {
                s << (UINT32)arr[j];

                if (j != arr.size() - 1) {
                    s << ", ";
                }
            }

            s << "]";

            if (!lastInStruct) {
                s << ",";
            }

            s << endl;
            return;
        }

        default:
            _ASSERT(FALSE);
    }
}

void OutStructSerializer::StartStruct(string _name)
{
    switch (tp) {
        case SerializationType::Text: {
            s << "class " << _name << endl;
            s << spaces(indent) << "{" << endl;
            indent++;
            return;
        }

        case SerializationType::JSON: {
            s << "{" << endl;
            indent++;
            return;
        }

        default:
            _ASSERT(FALSE);
    }

}

void OutStructSerializer::EndStruct(string _name)
{
    switch (tp) {
        case SerializationType::Text: {
            indent--;
            s << spaces(indent) << "}"  ;
            return;
        }

        case SerializationType::JSON: {
            indent--;
            s << spaces(indent) << "}";
            return;
        }

        default:
            _ASSERT(FALSE);
    }
}

void OutStructSerializer::StartArray(int count)
{
    switch (tp) {
        case SerializationType::Text: {
            s << endl << spaces(indent) << "[" << endl;
            indent++;
            return;
        }

        case SerializationType::JSON: {
            s << endl << spaces(indent) << "[" << endl;
            indent++;
            return;
        }

        default:
            _ASSERT(FALSE);
    }
}

void OutStructSerializer::EndArray()
{
    switch (tp) {
        case SerializationType::Text: {
            indent--;
            s << endl << spaces(indent) << "]" << endl;
            return;
        }

        case SerializationType::JSON: {
            indent--;
            s << endl << spaces(indent) << "]" << endl;
            return;
        }

        default:
            _ASSERT(FALSE);
    }
}

string OutStructSerializer::ToString()
{
    return s.str();
}

//
// InStructSerializer
//

vector<char> SkipChars {  ' ', '\t', '\r', '\n' };

InStructSerializer::InStructSerializer(SerializationType _tp, string  _s)
{
    tp = _tp;
    string ss;

    for (size_t j = 0; j < _s.size(); j++) {
        bool skipChar = false;

        for (auto i = SkipChars.begin(); i != SkipChars.end(); i++) {
            if (*i == _s[j]) {
                skipChar = true;
            }
            break;
        }

        if (skipChar) {
            continue;
        }

        ss += _s[j];
    }

    s.str(ss);
    debugString = ss;
}

// TODO: This is very JSON specific.
bool  InStructSerializer::DeSerialize(TpmStructureBase *p)
{
    if (!((tp == SerializationType::JSON))) {
        throw runtime_error("Not implemented");
    }

    int xx;
    const TpmTypeId tpId = p->GetTypeId();
    StructMarshallInfo *tpInfo = TypeMap[tpId];
    std::vector<MarshallInfo>& fields = tpInfo->Fields;

    if (!StartStruct()) {
        return false;
    }

    UINT64 val;
    TpmStructureBase *yy;

    for (int j = 0; j < (int)fields.size(); j++) {
        void *fieldPtr = p->ElementInfo(j, -1, xx, yy, -1);
        MarshallInfo& fInfo = fields[j];

        string elementName;

        if (!GetElementName(elementName)) {
            return false;
        }

        int xx;

        if (fInfo.IsArray) {
            if (!NextChar('[')) {
                return false;
            }

            // The array size has already been set
            int arrayCount;
            TpmStructureBase *yy;
            p->ElementInfo(j, -1, arrayCount, yy,  -1);

            for (int c = 0; c < arrayCount; c++) {
                switch (fInfo.Sort) {
                    case ElementSort::TpmStruct: {
                        TpmStructureBase *pStruct = NULL;
                        TpmStructureBase *elem = (TpmStructureBase *) p->ElementInfo(j, c, xx, pStruct, -1);

                        if (pStruct != NULL) {
                            elem = pStruct;
                        }

                        if (!DeSerialize(elem)) {
                            return false;
                        }

                        break;
                    }

                    case ElementSort::TpmUnion: {
                        TpmStructureBase *yy;
                        TpmStructureBase **elem = (TpmStructureBase **)p->ElementInfo(j, c, xx, yy,  -1);

                        if (!DeSerialize(*elem)) {
                            return false;
                        }

                        break;
                    }

                    case ElementSort::TpmValueType:
                    case ElementSort::TpmEnum:
                    case ElementSort::TpmBitfield: {
                        TpmStructureBase *yy;
                        void *elem = p->ElementInfo(j, c, xx, yy, -1);

                        if (!GetInteger(val, fInfo.ElementSize)) {
                            return false;
                        }

                        memcpy(elem, &val, fInfo.ElementSize);
                        break;
                    }

                    default:
                        _ASSERT(FALSE);
                }

                if (c != arrayCount - 1) {
                    if (!NextChar(',')) {
                        return false;
                    }
                }
            }

            if (!NextChar(']')) {
                return false;
            }

            goto EndProcessElement; // End of processing array element
        }

        // Else not an array
        switch (fInfo.Sort) {
            case ElementSort::TpmStruct: {
                TpmStructureBase *yy;
                TpmStructureBase *elem = (TpmStructureBase *)p->ElementInfo(j, -1, xx, yy, -1);

                if (!DeSerialize(elem)) {
                    return false;
                }

                goto EndProcessElement;
            }

            case ElementSort::TpmUnion: {
                // Make a new object based on the selector
                MarshallInfo& unionSelector = tpInfo->Fields[fInfo.AssociatedElement];
                TpmStructureBase *yy;
                void *selectorPtr = p->ElementInfo(fInfo.AssociatedElement, -1, xx, yy, -1);
                UINT32 selectorVal = GetValFromBuf((BYTE *) selectorPtr, unionSelector.ElementSize);
                TpmTypeId typeOfUnion = TheTypeMap.GetStructTypeIdFromUnionSelector(fInfo.ThisElementType, selectorVal);

                // Make a new object
                void *pUnion;
                TpmStructureBase *newObj = TpmStructureBase::Factory(typeOfUnion, fInfo.ThisElementType, pUnion);
                // Copy the pointer to the union into the struct
                memcpy(fieldPtr, &pUnion, sizeof(pUnion));

                // And then deserialize the object itself
                if (!DeSerialize(newObj)) {
                    return false;
                }

                goto EndProcessElement;
            }

            case ElementSort::TpmValueType:
            case ElementSort::TpmEnum:
            case ElementSort::TpmBitfield: {
                void *elem = p->ElementInfo(j, -1, xx, yy, -1);

                if (!GetInteger(val, fInfo.ElementSize)) {
                    return false;
                }

                memcpy(elem, &val, fInfo.ElementSize);
                goto DoSpecialProcessing;
            }

            default:
                _ASSERT(FALSE);
        }

DoSpecialProcessing:

        // Special processing
        if (fInfo.ElementMarshallType == MarshallType::ArrayCount) {
            int valIs = (int)val;

            // Sanity checks
            if (valIs < 0 || valIs > 16536) {
                return false;
            }

            p->ElementInfo(j + 1, -1, xx, yy, (UINT32)val);
            goto EndProcessElement;
        }

        if (fInfo.ElementMarshallType == MarshallType::UnionSelector) {
            // This does not work here because more than one element
            // can depend on the same union selector.
            goto EndProcessElement;
        }

        if (fInfo.ElementMarshallType == MarshallType::SpecialVariableLengthArray) {
            int valIs = (int)val;
            TPM_ALG_ID algId = (TPM_ALG_ID)valIs;
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
                    return false;
            };

            TpmStructureBase *yy;

            p->ElementInfo(j + 1, -1, xx, yy, numBytes);

            goto EndProcessElement;
        }

EndProcessElement:
        if (j != (int)fields.size() - 1 && !NextChar(','))
            return false;
    }

    return NextChar('}');
}

bool InStructSerializer::StartStruct()
{
    switch (tp) {
        case SerializationType::JSON: {
            char c;
            s >> c;

            if (c != '{') {
                return false;
            }

            return true;
        }

        default:
            _ASSERT(FALSE);
    }

    return false;
}

bool InStructSerializer::GetElementName(string& name)
{
    switch (tp) {
        case SerializationType::JSON: {
            string elementName;

            if (!GetToken(':', elementName)) {
                return false;
            }

            if (elementName.size() < 3) {
                return false;
            }

            if (elementName[0] != '\"') {
                return false;
            }

            if (elementName[elementName.size() - 1] != '\"') {
                return false;
            }

            elementName = elementName.substr(1, elementName.size() - 2);
            name = elementName;
            return true;
        }

        default:
            _ASSERT(FALSE);
    }

    return false;
}

bool InStructSerializer::GetToken(char terminator, string& tokenName)
{
    switch (tp) {
        case SerializationType::JSON: {
            string tok;

            while (true) {
                if (s.eof()) {
                    return false;
                }

                char c;
                s >> c;

                if (c == terminator) {
                    break;
                }

                tok += c;
            }

            tokenName = tok;
            return true;
        }

        default:
            _ASSERT(FALSE);
    }

    return false;
}

bool InStructSerializer::NextChar(char needed)
{
    if (s.eof()) {
        return false;
    }

    char c;
    s >> c;
    return c == needed;
}

bool InStructSerializer::GetInteger(UINT64& val, int numBytes)
{
    string str;

    while (true) {
        if (s.eof()) {
            return false;
        }

        char c = s.peek();
        _ASSERT(c != '-');

        if (!((c >= '0') && (c <= '9'))) {
            break;
        };

        s >> c;

        str += c;
    }

    // TODO: Signed and len-check
    if (str == "") {
        DebugStream();
        _ASSERT(FALSE);
        return false;
    }

    UINT64 res = (UINT64) - 999;
    istringstream(str) >> res;

    // TODO: Fix.
    _ASSERT(res != (UINT64) - 999);
    val = res;

    return true;
}

void InStructSerializer::DebugStream()
{
    size_t pos = (size_t) s.tellg();
    cout << "DEBUG--------------" << endl;
    cout << debugString.substr(0, pos - 1) << endl;
    cout << "FUTURE--------------" << endl;
    cout << debugString.substr(pos - 1);
    cout << endl;
    return ;
}

_TPMCPP_END