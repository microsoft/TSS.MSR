/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

_TPMCPP_BEGIN

///<summary>Special marshalling indicators</summary>
enum class WireType {
    Normal,
    ArrayCount,
    UnionSelector,
    UnionObject,
    FixedLengthArray,
    VariableLengthArray,
    SpecialVariableLengthArray, // Used for TPMT_HA where the alg indirectly decides the length
    ConstantValue,
    LengthOfStruct,
    EncryptedVariableLengthArray
};

///<summary>MarshallInfo desribes a TPM structure element</summary>
class MarshalInfo {
    public:
        // What this element is
        TpmTypeId TypeId;

        // Member name (for pretty-printing)
        string Name;

        // How should we marshall it
        WireType MarshalType;

        // Index of associated element (e.g. type, array-count) [if relevant]
        int AssociatedField;

        // Type of the struct that this field is member of
        TpmTypeId ParentType;

        // Does this field represent an array?
        bool IsArray()
        {
            _ASSERT(MarshalType != WireType::FixedLengthArray);
            return MarshalType == WireType::VariableLengthArray
                || MarshalType == WireType::SpecialVariableLengthArray
                || MarshalType == WireType::EncryptedVariableLengthArray;
        }
};

///<summary>Type of the element</summary>
enum class TpmEntity {
    Any,
    Typedef,
    Enum,
    Bitfield,
    Struct,
    Union
};

typedef TpmStructure* (*TpmObjectFactory)();

///<summary> Defines properties of TPM types required to marshal/unmarshal them.
///
/// Such descriptor is instantiated for each TPM entity (structure, union, bitfield,
/// enumeration, typedef, data structures representing TPM command/response buffers).
/// For structures it describes their fields and how they are marshalled.
/// For unions it describes their implementation classes.
/// For enums and bitfields it maps their element values to their names.
///</summary>
class TpmTypeInfo
{
public:
    virtual ~TpmTypeInfo() {}

    // Sort of thing (struct, union, value type)
    TpmEntity Kind;

    // Short type name
    string Name;

    static void Init();
};

class TpmStructInfo : public TpmTypeInfo
{
public:
    virtual ~TpmStructInfo() {}

    // Function creating an instance of this type
    // Only valid for structures
    TpmObjectFactory Factory;

    // Structure field information
    // Only valid for structures
    vector<MarshalInfo> Fields;

    // Number of handles in command or response (only valid for REQUEST or RESPONSE structs)
    int HandleCount;

    // Number of handles that need auth (only valid for REQUEST structs)
    int AuthHandleCount;
};

class TpmUnionInfo : public TpmTypeInfo
{
public:
    virtual ~TpmUnionInfo() {}

    // These two only valid for unions
    vector<UINT32> UnionSelector;
    vector<TpmTypeId> UnionType;

    // Look up union selector given the selector ID
    UINT32 GetUnionSelectorFromTypId(TpmTypeId selectorId)
    {
        for (size_t i = 0; i < UnionSelector.size(); i++)
        {
            if (UnionType[i] == selectorId)
                return UnionSelector[i];
        }
        return (UINT32)-1;
    }

    // Look up the implementation structure for this union selector given the selector ID
    TpmTypeId GetStructTypeIdFromUnionSelector(UINT32 selector)
    {
        // TODO: Better data structure
        for (size_t i = 0; i < UnionSelector.size(); i++)
        {
            if (UnionSelector[i] == selector)
                return UnionType[i];
        }
        return TpmTypeId::None;
    }
};

class TpmTypedefInfo : public TpmTypeInfo
{
public:
    virtual ~TpmTypedefInfo() {}

    // Underlying integer size in bytes
    int Size;
};

// Used for both TPM enums and bitfields
class TpmEnumInfo : public TpmTypedefInfo
{
public:
    virtual ~TpmEnumInfo() {}

    std::map<UINT32, string> ConstNames;
};

// Used for both TPM enums and bitfields
class TpmBitfieldInfo : public TpmTypedefInfo
{
public:
    virtual ~TpmBitfieldInfo() {}

    std::map<UINT32, string> ConstNames;
};

_TPMCPP_END
