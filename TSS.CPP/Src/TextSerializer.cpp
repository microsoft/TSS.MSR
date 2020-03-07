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
    if (SerType == SerializationType::None)
        return "";

    if (SerType != SerializationType::Text && SerType != SerializationType::JSON)
        throw runtime_error("Not implemented");

    int xx;
    TpmStructureBase *pStruct;
    const TpmTypeId tid = p->GetTypeId();
    TpmStructInfo& typeInfo = GetTypeInfo<TpmEntity::Struct>(tid);
    vector<MarshalInfo>& fields = typeInfo.Fields;

    StartStruct(typeInfo.Name);

    for (int j = 0; j < (int)fields.size(); j++)
    {
        bool lastInStruct = j == (int)fields.size() - 1;
        void* fieldPtr = p->ElementInfo(j, -1, xx, pStruct, -1);

        if (pStruct != NULL)
            fieldPtr = pStruct;

        MarshalInfo& fInfo = fields[j];
        OutTypeAndName(TypeMap[fInfo.TypeId]->Name, fInfo.Name, fInfo.IsArray());

        if (!fInfo.IsArray())
        {
            OutValue(fInfo, fieldPtr, lastInStruct);
            continue;
        }

        // Special handling for byte-arrays
        if (fInfo.TypeId == TpmTypeId::BYTE_ID) {
            ByteVec *vec = static_cast<vector<BYTE>*>(fieldPtr);
            OutByteArray(*vec, lastInStruct);
            continue;
        }

        // Else iterate over array elements
        int arrayCount;
        p->ElementInfo(j, -1, arrayCount, pStruct, -1);
        StartArray(arrayCount);

        for (int c = 0; c < arrayCount; c++)
        {
            void *pElem = p->ElementInfo(j, c, xx, pStruct, -1);

            if (pStruct != NULL)
                pElem = pStruct;

            Indent();
            bool lastElem = c == arrayCount - 1;
            OutValue(fInfo, pElem, lastElem);
        }

        EndArray();
        if (!lastInStruct)
            OutArrayElementSeparator();
    }

    EndStruct(typeInfo.Name);
    return s.str();
}

void OutStructSerializer::OutTypeAndName(string elementType, string elementName, BOOL isArray)
{
    switch (SerType) {
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
    switch (SerType) {
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
    switch (SerType) {
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
    TpmTypeInfo& fieldType = GetTypeInfo<TpmEntity::Any>(field.TypeId);

    if (SerType == SerializationType::Text)
    {
        if (fieldType.Kind == TpmEntity::Struct)
        {
            Serialize((TpmStructureBase*)pElem);
            s << endl;
        }
        else if (fieldType.Kind == TpmEntity::Union)
        {
            Serialize((TpmStructureBase*)pElem);
            s << endl;
        }
        else
        {
            int fieldSize = GetTypeSize(field.TypeId);
            UINT64 val = GetValFromByteBuf((BYTE*)pElem, fieldSize);

            if (fieldType.Kind == TpmEntity::Typedef) {
                // Numeric
                s << "0x" << setfill('0') << setw(fieldSize * 2) << hex << val << 
                        " (" << dec << val << ")" << setw(1) << endl;
            } else {
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
    }
    else if (SerType == SerializationType::JSON)
    {
        if (fieldType.Kind == TpmEntity::Struct)
        {
            auto b = (TpmStructureBase*)pElem;
            Serialize(b);

            if (!lastInStruct)
                s << " , ";
            s << endl;
        }
        else if (fieldType.Kind == TpmEntity::Union)
        {
            auto elem = (TpmStructureBase*)pElem;
            Serialize(elem);

            if (!lastInStruct)
                s << " , ";
            s << endl;
        }
        else
        {
            UINT64 val = GetValFromByteBuf((BYTE *)pElem, GetTypeSize(field.TypeId));
            s << val;

            if (!lastInStruct)
                s << " , ";
            s << endl;
        }
    }
    else
        _ASSERT(FALSE);
} // OutStructSerializer::OutValue

void OutStructSerializer::OutByteArray(ByteVec& arr, bool lastInStruct)
{
    if (SerType == SerializationType::Text)
    {
        size_t size = arr.size();
        size_t maxSize = precise ? size + 1 : 17;

        if (size > maxSize)
            size = maxSize;

        s << "[";
        for (size_t j = 0; j < arr.size(); j++)
        {
            if ((j > maxSize) && (j < arr.size() - 4))
                continue;

            s << hex << setfill('0') << setw(2) << (UINT32)arr[j];

            if ((j % 4 == 3) && (j != arr.size() - 1))
                s << " ";

            if (j == maxSize)
                s << "...  ";
        }
        s << "]" << endl;
    }
    else if (SerType == SerializationType::JSON)
    {
        s << "[";
        for (size_t j = 0; j < arr.size(); j++)
        {
            s << (UINT32)arr[j];

            if (j != arr.size() - 1)
                s << ", ";
        }
        s << "]";
            
        if (!lastInStruct)
            s << ",";
        s << endl;
    }
    else
        _ASSERT(FALSE);
} // OutStructSerializer::OutByteArray()

void OutStructSerializer::StartStruct(string _name)
{
    if (SerType == SerializationType::Text)
    {
        s << "class " << _name << endl;
        s << spaces(indent) << "{" << endl;
        indent++;
    }
    else if (SerType == SerializationType::JSON)
    {
        s << "{" << endl;
        indent++;
    }
    else
        _ASSERT(FALSE);
}

void OutStructSerializer::EndStruct(string _name)
{
    if (SerType == SerializationType::Text)
    {
        indent--;
        s << spaces(indent) << "}"  ;
    }
    else if (SerType == SerializationType::JSON)
    {
        indent--;
        s << spaces(indent) << "}";
    }
    else
        _ASSERT(FALSE);
}

void OutStructSerializer::StartArray(int count)
{
    if (SerType == SerializationType::Text)
    {
        s << endl << spaces(indent) << "[" << endl;
        indent++;
    }
    else if (SerType == SerializationType::JSON)
    {
        s << endl << spaces(indent) << "[" << endl;
        indent++;
    }
    else
        _ASSERT(FALSE);
}

void OutStructSerializer::EndArray()
{
    if (SerType == SerializationType::Text)
    {
        indent--;
        s << endl << spaces(indent) << "]" << endl;
    }
    else if (SerType == SerializationType::JSON)
    {
        indent--;
        s << endl << spaces(indent) << "]" << endl;
    }
    else
        _ASSERT(FALSE);
}

//
// InStructSerializer
//

vector<char> SkipChars {  ' ', '\t', '\r', '\n' };

InStructSerializer::InStructSerializer(SerializationType _tp, string  _s)
{
    SerType = _tp;
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
    if (SerType != SerializationType::JSON)
        throw runtime_error("Not implemented");

    if (!StartStruct())
        return false;

    TpmStructInfo& typeInfo = GetTypeInfo<TpmEntity::Struct>(p->GetTypeId());
    vector<MarshalInfo>& fields = typeInfo.Fields;

    UINT64 val;
    int xx;
    TpmStructureBase *yy;

    for (int j = 0; j < (int)fields.size(); j++)
    {
        void *fieldPtr = p->ElementInfo(j, -1, xx, yy, -1);
        MarshalInfo& field = fields[j];
        TpmTypeInfo& fieldInfo = GetTypeInfo<TpmEntity::Any>(field.TypeId);

        string elementName;
        if (!GetElementName(elementName))
            return false;

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
                if (fieldInfo.Kind == TpmEntity::Struct)
                {
                    TpmStructureBase *pStruct = NULL;
                    TpmStructureBase *elem = (TpmStructureBase*)p->ElementInfo(j, c, xx, pStruct, -1);

                    _ASSERT(pStruct);
                    if (pStruct != NULL)
                        elem = pStruct;

                    if (!DeSerialize(elem))
                        return false;
                }
                else if (fieldInfo.Kind == TpmEntity::Union)
                {
                    TpmStructureBase **elem = (TpmStructureBase **)p->ElementInfo(j, c, xx, yy,  -1);
                    if (!DeSerialize(*elem))
                        return false;
                }
                else {
                    void *elem = p->ElementInfo(j, c, xx, yy, -1);
                    int fieldSize = GetTypeSize(field.TypeId);
                    if (!GetInteger(val, fieldSize))
                        return false;
                    memcpy(elem, &val, fieldSize);
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
        if (fieldInfo.Kind == TpmEntity::Struct)
        {
            TpmStructureBase *elem = (TpmStructureBase *)p->ElementInfo(j, -1, xx, yy, -1);

            if (!DeSerialize(elem))
                return false;
            goto EndProcessElement;
        }
        else if (fieldInfo.Kind == TpmEntity::Union)
        {
            // Make a new object based on the selector
            MarshalInfo& unionSelector = typeInfo.Fields[field.AssociatedField];
            void *selectorPtr = p->ElementInfo(field.AssociatedField, -1, xx, yy, -1);
            UINT32 selectorVal = GetValFromBuf((BYTE*)selectorPtr, GetTypeSize(unionSelector.TypeId));
            TpmTypeId typeOfUnion = GetTypeInfo<TpmEntity::Union>(field.TypeId)
                                        .GetStructTypeIdFromUnionSelector(selectorVal);

            // Make a new object
            TpmStructureBase *newObj = TpmStructureBase::UnionFactory(typeOfUnion, field.TypeId, fieldPtr);

            // Deserialize the object
            if (!DeSerialize(newObj))
                return false;
            goto EndProcessElement;
        }
        else
        {
            void *pField = p->ElementInfo(j, -1, xx, yy, -1);
            _ASSERT(pField == fieldPtr);
            int fieldSize = GetTypeSize(field.TypeId);
            if (!GetInteger(val, fieldSize))
                return false;
            memcpy(pField, &val, fieldSize);
            goto DoSpecialProcessing;
        }

DoSpecialProcessing:

        // Special processing
        if (field.MarshalType == MarshalType::ArrayCount)
        {
            UINT32 count = (UINT32)val;

            if (count > 16536)
                return false;

            p->ElementInfo(j + 1, -1, xx, yy, count);
            goto EndProcessElement;
        }

        if (field.MarshalType == MarshalType::UnionSelector) {
            // This does not work here because more than one element
            // can depend on the same union selector.
            goto EndProcessElement;
        }

        if (field.MarshalType == MarshalType::SpecialVariableLengthArray)
        {
            TPM_ALG_ID algId = (TPM_ALG_ID)(UINT32)val;
            int numBytes = CryptoServices::HashLength(algId);

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
    if (SerType == SerializationType::JSON)
    {
        char c;
        s >> c;
        return c == '{';
    }

    _ASSERT(FALSE);
    return false;
}

bool InStructSerializer::GetElementName(string& name)
{
    if (SerType == SerializationType::JSON)
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

    _ASSERT(FALSE);
    return false;
}

bool InStructSerializer::GetToken(char terminator, string& tokenName)
{
    if (SerType == SerializationType::JSON)
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
    _ASSERT(FALSE);
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
    while (true)
    {
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
