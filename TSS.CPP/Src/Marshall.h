/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once
_TPMCPP_BEGIN

///<summary>Special marshalling indicators</summary>
enum class MarshallType {
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

///<summary>Type of the element</summary>
enum class ElementSort {
    TpmEnum,
    TpmBitfield,
    TpmTypedef,
    TpmValueType,
    FixedLengthArray,
    TpmStruct,
    TpmUnion
};

typedef TpmStructureBase *(*ObjectFactory)();

///<summary>MarshallInfo desribes a TPM structure element</summary>
class MarshallInfo {
    public:
        // For debugging
        std::string ElementName;

        // Element size in bytes
        int ElementSize;

        // What this element is
        TpmTypeId ThisElementType;

        // This element type
        std::string ThisElementTypeName;

        // Sort of thing (struct, number, union..)
        ElementSort Sort;

        // Array?
        BOOL IsArray;

        // How should we marshall it
        MarshallType ElementMarshallType;

        // Index of associated element (e.g. type, array-count) [if relevant]
        int AssociatedElement;

        // Parent type name
        std::string ParentTypeName;

        // Type of the struct that this element is part of
        TpmTypeId ParentType;
};


///<summary>One of these for each TPM structure, bitfield and enumeration.
///For structures it describes the elements and how they should be marshalled.
///For enums it maps enum and bitfield values to strings for debugging, printing,
///serialization, etc.</summary>
class StructMarshallInfo {
    public:
        // C++ typeid
        std::string Name;

        // TpmTypeId
        //TpmTypeId MyId;

        // C++ type_info.name()
        string MyTypeInfo;

        // Structure field information
        std::vector<MarshallInfo> Fields;

        // These two only valid for unions
        std::vector<UINT32> UnionSelector;
        std::vector<TpmTypeId> UnionType;

        // This is valid for bitfields
        std::vector<std::string> BitNames;

        // Valid for enumerations
        std::map<UINT32, std::string> EnumNames;

        // Number of handles in command or response (only valid for REQUEST or RESPONSE structs)
        int HandleCount;

        // Number of handles that need auth (only valid for REQUEST structs)
        int AuthHandleCount;

        // Class factory

        // ObjectFactory Factory;
        static void TpmTypeInitter();
};

class TpmTypeMap {
    public:
        // Look up marshalling info by name
        StructMarshallInfo *GetStructMarshallInfo(TpmTypeId id);

        // Look up marshalling info by type_info.name()
        StructMarshallInfo *GetStructMarshallInfo(const string& tyeInfoName);

        // Look up union selector given union type
        UINT32 GetUnionSelectorFromTypId(TpmTypeId unionId, TpmTypeId selectorId);
        TpmTypeId GetStructTypeIdFromUnionSelector(TpmTypeId unionId, UINT32 selector);

        void Init();

    protected:
        map<string, StructMarshallInfo *> TypeInfoNameMap;
};

extern TpmTypeMap TheTypeMap;
extern std::map<TpmTypeId, StructMarshallInfo *> TypeMap;

_TPMCPP_END