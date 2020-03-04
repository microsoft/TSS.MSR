/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"

_TPMCPP_BEGIN

using namespace std;

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
    const TpmTypeId tid = p->GetTypeId();
    TpmTypeInfo *typeInfo = TypeMap[tid];
    vector<MarshalInfo>& fields = typeInfo->Fields;

    StartStruct(typeInfo->Name);

    for (int j = 0; j < (int)fields.size(); j++) {
        bool lastInStruct = j == (int)fields.size() - 1;
        void *fieldPtr = p->ElementInfo(j, -1, xx, yy, -1);

        if (yy != NULL) {
            fieldPtr = yy;
        }

        MarshalInfo& fInfo = fields[j];
        OutTypeAndName(TypeMap[fInfo.TypeId]->Name, fInfo.Name, fInfo.IsArray());

        if (fInfo.IsArray()) {
            // Special handling for byte-arrays
            if (fInfo.TypeId == TpmTypeId::BYTE_ID) {
                ByteVec *vec = static_cast<vector<BYTE>*>(fieldPtr);
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

    EndStruct(typeInfo->Name);
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

void OutStructSerializer::OutValue(MarshalInfo& field, void *pElem, bool lastInStruct)
{
    TpmTypeInfo& fieldInfo = *TypeMap[field.TypeId];

    switch (tp) {
        case SerializationType::Text:
        {
            if (fieldInfo.Kind == TpmTypeKind::TpmStruct)
            {
                Serialize((TpmStructureBase*)pElem);
                s << endl;
            }
            else if (fieldInfo.Kind == TpmTypeKind::TpmUnion)
            {
                Serialize((TpmStructureBase*)pElem);
                s << endl;
            }
            else
            {
                UINT64 val = GetValFromByteBuf((BYTE*)pElem, fieldInfo.Size);

                if (fieldInfo.Kind == TpmTypeKind::TpmValueType) {
                    // Numeric
                    s << "0x" << setfill('0') << setw(fieldInfo.Size) << hex << val << 
                         " (" << dec << val << ")" << setw(1) << endl;
                }
                else {
                    // Convert enum to string
                    string enumString = GetEnumString((UINT32)val, field.TypeId);
                    int col = GetColumn(s);
                    string lineFeed = "";

                    if (enumString.find('|') != string::npos) {
                        enumString = AlignToColumns(enumString, '|', col);
                        lineFeed = "\n" + string(col, ' ');
                    }

                    s << enumString << lineFeed << " (0x" << hex << val << ")" << endl;
                }

            }
            return;
        }

        case SerializationType::JSON:
        {
            if (fieldInfo.Kind == TpmTypeKind::TpmStruct)
            {
                auto b = (TpmStructureBase*)pElem;
                Serialize(b);

                if (!lastInStruct)
                    s << " , ";
                s << endl;
            }
            else if (fieldInfo.Kind == TpmTypeKind::TpmUnion)
            {
                auto elem = (TpmStructureBase*)pElem;
                Serialize(elem);

                if (!lastInStruct)
                    s << " , ";
                s << endl;
            }
            else
            {
                UINT64 val = GetValFromByteBuf((BYTE *)pElem, fieldInfo.Size);
                s << val;

                if (!lastInStruct)
                    s << " , ";
                s << endl;
            }
            return;
        }
        default:
            _ASSERT(FALSE);
    }
}

void OutStructSerializer::OutByteArray(ByteVec& arr, bool lastInStruct)
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
    if (tp != SerializationType::JSON)
        throw runtime_error("Not implemented");

    int xx;
    const TpmTypeId tpId = p->GetTypeId();
    TpmTypeInfo *tpInfo = TypeMap[tpId];
    vector<MarshalInfo>& fields = tpInfo->Fields;

    if (!StartStruct()) {
        return false;
    }

    UINT64 val;
    TpmStructureBase *yy;

    for (int j = 0; j < (int)fields.size(); j++)
    {
        void *fieldPtr = p->ElementInfo(j, -1, xx, yy, -1);
        MarshalInfo& field = fields[j];
        TpmTypeInfo& fieldInfo = *TypeMap[field.TypeId];

        string elementName;
        if (!GetElementName(elementName))
            return false;

        int xx;
        if (field.IsArray())
        {
            if (!NextChar('['))
                return false;

            // The array size has already been set
            int arrayCount;
            TpmStructureBase *yy;
            p->ElementInfo(j, -1, arrayCount, yy,  -1);

            for (int c = 0; c < arrayCount; c++)
            {
                if (fieldInfo.Kind == TpmTypeKind::TpmStruct)
                {
                    TpmStructureBase *pStruct = NULL;
                    TpmStructureBase *elem = (TpmStructureBase*)p->ElementInfo(j, c, xx, pStruct, -1);

                    if (pStruct != NULL)
                        elem = pStruct;

                    if (!DeSerialize(elem))
                        return false;
                }
                else if (fieldInfo.Kind == TpmTypeKind::TpmUnion)
                {
                    TpmStructureBase *yy;
                    TpmStructureBase **elem = (TpmStructureBase **)p->ElementInfo(j, c, xx, yy,  -1);
                    if (!DeSerialize(*elem))
                        return false;
                }
                else {
                    TpmStructureBase *yy;
                    void *elem = p->ElementInfo(j, c, xx, yy, -1);

                    if (!GetInteger(val, fieldInfo.Size))
                        return false;

                    memcpy(elem, &val, fieldInfo.Size);
                }

                if (c != arrayCount - 1) {
                    if (!NextChar(','))
                        return false;
                }
            }

            if (!NextChar(']'))
                return false;

            goto EndProcessElement; // End of processing array element
        }

        // Else not an array
        if (fieldInfo.Kind == TpmTypeKind::TpmStruct)
        {
            TpmStructureBase *yy;
            TpmStructureBase *elem = (TpmStructureBase *)p->ElementInfo(j, -1, xx, yy, -1);

            if (!DeSerialize(elem))
                return false;
            goto EndProcessElement;
        }
        else if (fieldInfo.Kind == TpmTypeKind::TpmUnion)
        {
            // Make a new object based on the selector
            MarshalInfo& unionSelector = tpInfo->Fields[field.AssociatedField];
            TpmStructureBase *yy;
            void *selectorPtr = p->ElementInfo(field.AssociatedField, -1, xx, yy, -1);
            UINT32 selectorVal = GetValFromBuf((BYTE *) selectorPtr, TypeMap[unionSelector.TypeId]->Size);
            TpmTypeId typeOfUnion = TypeMap[field.TypeId]->GetStructTypeIdFromUnionSelector(selectorVal);

            // Make a new object
            TpmStructureBase *newObj = TpmStructureBase::UnionFactory(typeOfUnion, field.TypeId, fieldPtr);

            // Deserialize the object
            if (!DeSerialize(newObj))
                return false;
            goto EndProcessElement;
        }
        else
        {
            void *elem = p->ElementInfo(j, -1, xx, yy, -1);

            if (!GetInteger(val, fieldInfo.Size))
                return false;

            memcpy(elem, &val, fieldInfo.Size);
            goto DoSpecialProcessing;
        }

DoSpecialProcessing:

        // Special processing
        if (field.MarshalType == MarshalType::ArrayCount) {
            int valIs = (int)val;

            // Sanity checks
            if (valIs < 0 || valIs > 16536)
                return false;

            p->ElementInfo(j + 1, -1, xx, yy, (UINT32)val);
            goto EndProcessElement;
        }

        if (field.MarshalType == MarshalType::UnionSelector) {
            // This does not work here because more than one element
            // can depend on the same union selector.
            goto EndProcessElement;
        }

        if (field.MarshalType == MarshalType::SpecialVariableLengthArray) {
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
            return c == '{';
        }

        default:
            _ASSERT(FALSE);
    }
    return false;
}

bool InStructSerializer::GetElementName(string& name)
{
    switch (tp) {
        case SerializationType::JSON:
        {
            string elementName;

            if (!GetToken(':', elementName))
                return false;

            if (elementName.size() < 3)
                return false;

            if (elementName[0] != '\"')
                return false;

            if (elementName[elementName.size() - 1] != '\"')
                return false;

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
        case SerializationType::JSON:
        {
            string tok;
            while (true) {
                if (s.eof())
                    return false;

                char c;
                s >> c;
                if (c == terminator)
                    break;
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
    if (s.eof())
        return false;

    char c;
    s >> c;
    return c == needed;
}

bool InStructSerializer::GetInteger(UINT64& val, int numBytes)
{
    string str;
    while (true) {
        if (s.eof())
            return false;

        char c = s.peek();
        _ASSERT(c != '-');

        if (!((c >= '0') && (c <= '9')))
            break;

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
