package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* The TPM_PT constants are used in TPM2_GetCapability(capability = TPM_CAP_TPM_PROPERTIES) to indicate the property being selected or returned.
*/
public final class TPM_PT extends TpmEnum<TPM_PT>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_PT. qualifier.
    public enum _N {
        /**
        * indicates no property type
        */
        NONE,
        
        /**
        * The number of properties in each group. NOTE The first group with any properties is group 1 (PT_GROUP * 1). Group 0 is reserved.
        */
        PT_GROUP,
        
        /**
        * the group of fixed properties returned as TPMS_TAGGED_PROPERTY The values in this group are only changed due to a firmware change in the TPM.
        */
        PT_FIXED,
        
        /**
        * a 4-octet character string containing the TPM Family value (TPM_SPEC_FAMILY)
        */
        FAMILY_INDICATOR,
        
        /**
        * the level of the specification NOTE 1 For this specification, the level is zero. NOTE 2 The level is on the title page of the specification.
        */
        LEVEL,
        
        /**
        * the specification Revision times 100 EXAMPLE Revision 01.01 would have a value of 101. NOTE The Revision value is on the title page of the specification.
        */
        REVISION,
        
        /**
        * the specification day of year using TCG calendar EXAMPLE November 15, 2010, has a day of year value of 319 (0000013F16). NOTE The specification date is on the title page of the specification or errata. S(see 6.1).
        */
        DAY_OF_YEAR,
        
        /**
        * the specification year using the CE EXAMPLE The year 2010 has a value of 000007DA16. NOTE The specification date is on the title page of the specification or errata. S(see 6.1).
        */
        YEAR,
        
        /**
        * the vendor ID unique to each TPM manufacturer
        */
        MANUFACTURER,
        
        /**
        * the first four characters of the vendor ID string NOTE When the vendor string is fewer than 16 octets, the additional property values do not have to be present. A vendor string of 4 octets can be represented in one 32-bit value and no null terminating character is required.
        */
        VENDOR_STRING_1,
        
        /**
        * the second four characters of the vendor ID string
        */
        VENDOR_STRING_2,
        
        /**
        * the third four characters of the vendor ID string
        */
        VENDOR_STRING_3,
        
        /**
        * the fourth four characters of the vendor ID sting
        */
        VENDOR_STRING_4,
        
        /**
        * vendor-defined value indicating the TPM model
        */
        VENDOR_TPM_TYPE,
        
        /**
        * the most-significant 32 bits of a TPM vendor-specific value indicating the version number of the firmware. See 10.12.2 and 10.12.8.
        */
        FIRMWARE_VERSION_1,
        
        /**
        * the least-significant 32 bits of a TPM vendor-specific value indicating the version number of the firmware. See 10.12.2 and 10.12.8.
        */
        FIRMWARE_VERSION_2,
        
        /**
        * the maximum size of a parameter (typically, a TPM2B_MAX_BUFFER)
        */
        INPUT_BUFFER,
        
        /**
        * the minimum number of transient objects that can be held in TPM RAM NOTE This minimum shall be no less than the minimum value required by the platform-specific specification to which the TPM is built.
        */
        HR_TRANSIENT_MIN,
        
        /**
        * the minimum number of persistent objects that can be held in TPM NV memory NOTE This minimum shall be no less than the minimum value required by the platform-specific specification to which the TPM is built.
        */
        HR_PERSISTENT_MIN,
        
        /**
        * the minimum number of authorization sessions that can be held in TPM RAM NOTE This minimum shall be no less than the minimum value required by the platform-specific specification to which the TPM is built.
        */
        HR_LOADED_MIN,
        
        /**
        * the number of authorization sessions that may be active at a time A session is active when it has a context associated with its handle. The context may either be in TPM RAM or be context saved. NOTE This value shall be no less than the minimum value required by the platform-specific specification to which the TPM is built.
        */
        ACTIVE_SESSIONS_MAX,
        
        /**
        * the number of PCR implemented NOTE This number is determined by the defined attributes, not the number of PCR that are populated.
        */
        PCR_COUNT,
        
        /**
        * the minimum number of octets in a TPMS_PCR_SELECT.sizeOfSelect NOTE This value is not determined by the number of PCR implemented but by the number of PCR required by the platform-specific specification with which the TPM is compliant or by the implementer if not adhering to a platform-specific specification.
        */
        PCR_SELECT_MIN,
        
        /**
        * the maximum allowed difference (unsigned) between the contextID values of two saved session contexts This value shall be 2n-1, where n is at least 16.
        */
        CONTEXT_GAP_MAX,
        
        /**
        * the maximum number of NV Indexes that are allowed to have the TPM_NT_COUNTER attribute NOTE It is allowed for this value to be larger than the number of NV Indexes that can be defined. This would be indicative of a TPM implementation that did not use different implementation technology for different NV Index types.
        */
        NV_COUNTERS_MAX,
        
        /**
        * the maximum size of an NV Index data area
        */
        NV_INDEX_MAX,
        
        /**
        * a TPMA_MEMORY indicating the memory management method for the TPM
        */
        MEMORY,
        
        /**
        * interval, in milliseconds, between updates to the copy of TPMS_CLOCK_INFO.clock in NV
        */
        CLOCK_UPDATE,
        
        /**
        * the algorithm used for the integrity HMAC on saved contexts and for hashing the fuData of TPM2_FirmwareRead()
        */
        CONTEXT_HASH,
        
        /**
        * TPM_ALG_ID, the algorithm used for encryption of saved contexts
        */
        CONTEXT_SYM,
        
        /**
        * TPM_KEY_BITS, the size of the key used for encryption of saved contexts
        */
        CONTEXT_SYM_SIZE,
        
        /**
        * the modulus - 1 of the count for NV update of an orderly counter The returned value is MAX_ORDERLY_COUNT. This will have a value of 2N 1 where 1 N 32 NOTE 1 An orderly counter is an NV Index with an TPM_NT of TPM_NV_COUNTER and TPMA_NV_ORDERLY SET. NOTE 2 When the low-order bits of a counter equal this value, an NV write occurs on the next increment.
        */
        ORDERLY_COUNT,
        
        /**
        * the maximum value for commandSize in a command
        */
        MAX_COMMAND_SIZE,
        
        /**
        * the maximum value for responseSize in a response
        */
        MAX_RESPONSE_SIZE,
        
        /**
        * the maximum size of a digest that can be produced by the TPM
        */
        MAX_DIGEST,
        
        /**
        * the maximum size of an object context that will be returned by TPM2_ContextSave
        */
        MAX_OBJECT_CONTEXT,
        
        /**
        * the maximum size of a session context that will be returned by TPM2_ContextSave
        */
        MAX_SESSION_CONTEXT,
        
        /**
        * platform-specific family (a TPM_PS value)(see Table 25) NOTE The platform-specific values for the TPM_PT_PS parameters are in the relevant platform-specific specification. In the reference implementation, all of these values are 0.
        */
        PS_FAMILY_INDICATOR,
        
        /**
        * the level of the platform-specific specification
        */
        PS_LEVEL,
        
        /**
        * the specification Revision times 100 for the platform-specific specification EXAMPLE Revision 01.01 would have a value of 101.
        */
        PS_REVISION,
        
        /**
        * the platform-specific TPM specification day of year using TCG calendar EXAMPLE November 15, 2010, has a day of year value of 319 (0000013F16).
        */
        PS_DAY_OF_YEAR,
        
        /**
        * the platform-specific TPM specification year using the CE EXAMPLE The year 2010 has a value of 000007DA16.
        */
        PS_YEAR,
        
        /**
        * the number of split signing operations supported by the TPM
        */
        SPLIT_MAX,
        
        /**
        * total number of commands implemented in the TPM
        */
        TOTAL_COMMANDS,
        
        /**
        * number of commands from the TPM library that are implemented
        */
        LIBRARY_COMMANDS,
        
        /**
        * number of vendor commands that are implemented
        */
        VENDOR_COMMANDS,
        
        /**
        * the maximum data size in one NV write or NV read command
        */
        NV_BUFFER_MAX,
        
        /**
        * a TPMA_MODES value, indicating that the TPM is designed for these modes.
        */
        MODES,
        
        /**
        * the maximum size of a TPMS_CAPABILITY_DATA structure returned in TPM2_GetCapability().
        */
        MAX_CAP_BUFFER,
        
        /**
        * the group of variable properties returned as TPMS_TAGGED_PROPERTY The properties in this group change because of a Protected Capability other than a firmware update. The values are not necessarily persistent across all power transitions.
        */
        PT_VAR,
        
        /**
        * TPMA_PERMANENT
        */
        PERMANENT,
        
        /**
        * TPMA_STARTUP_CLEAR
        */
        STARTUP_CLEAR,
        
        /**
        * the number of NV Indexes currently defined
        */
        HR_NV_INDEX,
        
        /**
        * the number of authorization sessions currently loaded into TPM RAM
        */
        HR_LOADED,
        
        /**
        * the number of additional authorization sessions, of any type, that could be loaded into TPM RAM This value is an estimate. If this value is at least 1, then at least one authorization session of any type may be loaded. Any command that changes the RAM memory allocation can make this estimate invalid. NOTE A valid implementation may return 1 even if more than one authorization session would fit into RAM.
        */
        HR_LOADED_AVAIL,
        
        /**
        * the number of active authorization sessions currently being tracked by the TPM This is the sum of the loaded and saved sessions.
        */
        HR_ACTIVE,
        
        /**
        * the number of additional authorization sessions, of any type, that could be created This value is an estimate. If this value is at least 1, then at least one authorization session of any type may be created. Any command that changes the RAM memory allocation can make this estimate invalid. NOTE A valid implementation may return 1 even if more than one authorization session could be created.
        */
        HR_ACTIVE_AVAIL,
        
        /**
        * estimate of the number of additional transient objects that could be loaded into TPM RAM This value is an estimate. If this value is at least 1, then at least one object of any type may be loaded. Any command that changes the memory allocation can make this estimate invalid. NOTE A valid implementation may return 1 even if more than one transient object would fit into RAM.
        */
        HR_TRANSIENT_AVAIL,
        
        /**
        * the number of persistent objects currently loaded into TPM NV memory
        */
        HR_PERSISTENT,
        
        /**
        * the number of additional persistent objects that could be loaded into NV memory This value is an estimate. If this value is at least 1, then at least one object of any type may be made persistent. Any command that changes the NV memory allocation can make this estimate invalid. NOTE A valid implementation may return 1 even if more than one persistent object would fit into NV memory.
        */
        HR_PERSISTENT_AVAIL,
        
        /**
        * the number of defined NV Indexes that have NV the TPM_NT_COUNTER attribute
        */
        NV_COUNTERS,
        
        /**
        * the number of additional NV Indexes that can be defined with their TPM_NT of TPM_NV_COUNTER and the TPMA_NV_ORDERLY attribute SET This value is an estimate. If this value is at least 1, then at least one NV Index may be created with a TPM_NT of TPM_NV_COUNTER and the TPMA_NV_ORDERLY attributes. Any command that changes the NV memory allocation can make this estimate invalid. NOTE A valid implementation may return 1 even if more than one NV counter could be defined.
        */
        NV_COUNTERS_AVAIL,
        
        /**
        * code that limits the algorithms that may be used with the TPM
        */
        ALGORITHM_SET,
        
        /**
        * the number of loaded ECC curves
        */
        LOADED_CURVES,
        
        /**
        * the current value of the lockout counter (failedTries)
        */
        LOCKOUT_COUNTER,
        
        /**
        * the number of authorization failures before DA lockout is invoked
        */
        MAX_AUTH_FAIL,
        
        /**
        * the number of seconds before the value reported by TPM_PT_LOCKOUT_COUNTER is decremented
        */
        LOCKOUT_INTERVAL,
        
        /**
        * the number of seconds after a lockoutAuth failure before use of lockoutAuth may be attempted again
        */
        LOCKOUT_RECOVERY,
        
        /**
        * number of milliseconds before the TPM will accept another command that will modify NV This value is an approximation and may go up or down over time.
        */
        NV_WRITE_RECOVERY,
        
        /**
        * the high-order 32 bits of the command audit counter
        */
        AUDIT_COUNTER_0,
        
        /**
        * the low-order 32 bits of the command audit counter
        */
        AUDIT_COUNTER_1
        
    }
    
    private static ValueMap<TPM_PT> _ValueMap = new ValueMap<TPM_PT>();
    
    public static final TPM_PT
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        NONE = new TPM_PT(0x00000000, _N.NONE),
        PT_GROUP = new TPM_PT(0x00000100, _N.PT_GROUP),
        PT_FIXED = new TPM_PT(TPM_PT.PT_GROUP.toInt() * 1, _N.PT_FIXED),
        FAMILY_INDICATOR = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 0, _N.FAMILY_INDICATOR),
        LEVEL = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 1, _N.LEVEL),
        REVISION = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 2, _N.REVISION),
        DAY_OF_YEAR = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 3, _N.DAY_OF_YEAR),
        YEAR = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 4, _N.YEAR),
        MANUFACTURER = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 5, _N.MANUFACTURER),
        VENDOR_STRING_1 = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 6, _N.VENDOR_STRING_1),
        VENDOR_STRING_2 = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 7, _N.VENDOR_STRING_2),
        VENDOR_STRING_3 = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 8, _N.VENDOR_STRING_3),
        VENDOR_STRING_4 = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 9, _N.VENDOR_STRING_4),
        VENDOR_TPM_TYPE = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 10, _N.VENDOR_TPM_TYPE),
        FIRMWARE_VERSION_1 = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 11, _N.FIRMWARE_VERSION_1),
        FIRMWARE_VERSION_2 = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 12, _N.FIRMWARE_VERSION_2),
        INPUT_BUFFER = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 13, _N.INPUT_BUFFER),
        HR_TRANSIENT_MIN = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 14, _N.HR_TRANSIENT_MIN),
        HR_PERSISTENT_MIN = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 15, _N.HR_PERSISTENT_MIN),
        HR_LOADED_MIN = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 16, _N.HR_LOADED_MIN),
        ACTIVE_SESSIONS_MAX = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 17, _N.ACTIVE_SESSIONS_MAX),
        PCR_COUNT = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 18, _N.PCR_COUNT),
        PCR_SELECT_MIN = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 19, _N.PCR_SELECT_MIN),
        CONTEXT_GAP_MAX = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 20, _N.CONTEXT_GAP_MAX),
        NV_COUNTERS_MAX = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 22, _N.NV_COUNTERS_MAX),
        NV_INDEX_MAX = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 23, _N.NV_INDEX_MAX),
        MEMORY = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 24, _N.MEMORY),
        CLOCK_UPDATE = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 25, _N.CLOCK_UPDATE),
        CONTEXT_HASH = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 26, _N.CONTEXT_HASH),
        CONTEXT_SYM = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 27, _N.CONTEXT_SYM),
        CONTEXT_SYM_SIZE = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 28, _N.CONTEXT_SYM_SIZE),
        ORDERLY_COUNT = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 29, _N.ORDERLY_COUNT),
        MAX_COMMAND_SIZE = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 30, _N.MAX_COMMAND_SIZE),
        MAX_RESPONSE_SIZE = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 31, _N.MAX_RESPONSE_SIZE),
        MAX_DIGEST = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 32, _N.MAX_DIGEST),
        MAX_OBJECT_CONTEXT = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 33, _N.MAX_OBJECT_CONTEXT),
        MAX_SESSION_CONTEXT = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 34, _N.MAX_SESSION_CONTEXT),
        PS_FAMILY_INDICATOR = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 35, _N.PS_FAMILY_INDICATOR),
        PS_LEVEL = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 36, _N.PS_LEVEL),
        PS_REVISION = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 37, _N.PS_REVISION),
        PS_DAY_OF_YEAR = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 38, _N.PS_DAY_OF_YEAR),
        PS_YEAR = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 39, _N.PS_YEAR),
        SPLIT_MAX = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 40, _N.SPLIT_MAX),
        TOTAL_COMMANDS = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 41, _N.TOTAL_COMMANDS),
        LIBRARY_COMMANDS = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 42, _N.LIBRARY_COMMANDS),
        VENDOR_COMMANDS = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 43, _N.VENDOR_COMMANDS),
        NV_BUFFER_MAX = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 44, _N.NV_BUFFER_MAX),
        MODES = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 45, _N.MODES),
        MAX_CAP_BUFFER = new TPM_PT(TPM_PT.PT_FIXED.toInt() + 46, _N.MAX_CAP_BUFFER),
        PT_VAR = new TPM_PT(TPM_PT.PT_GROUP.toInt() * 2, _N.PT_VAR),
        PERMANENT = new TPM_PT(TPM_PT.PT_VAR.toInt() + 0, _N.PERMANENT),
        STARTUP_CLEAR = new TPM_PT(TPM_PT.PT_VAR.toInt() + 1, _N.STARTUP_CLEAR),
        HR_NV_INDEX = new TPM_PT(TPM_PT.PT_VAR.toInt() + 2, _N.HR_NV_INDEX),
        HR_LOADED = new TPM_PT(TPM_PT.PT_VAR.toInt() + 3, _N.HR_LOADED),
        HR_LOADED_AVAIL = new TPM_PT(TPM_PT.PT_VAR.toInt() + 4, _N.HR_LOADED_AVAIL),
        HR_ACTIVE = new TPM_PT(TPM_PT.PT_VAR.toInt() + 5, _N.HR_ACTIVE),
        HR_ACTIVE_AVAIL = new TPM_PT(TPM_PT.PT_VAR.toInt() + 6, _N.HR_ACTIVE_AVAIL),
        HR_TRANSIENT_AVAIL = new TPM_PT(TPM_PT.PT_VAR.toInt() + 7, _N.HR_TRANSIENT_AVAIL),
        HR_PERSISTENT = new TPM_PT(TPM_PT.PT_VAR.toInt() + 8, _N.HR_PERSISTENT),
        HR_PERSISTENT_AVAIL = new TPM_PT(TPM_PT.PT_VAR.toInt() + 9, _N.HR_PERSISTENT_AVAIL),
        NV_COUNTERS = new TPM_PT(TPM_PT.PT_VAR.toInt() + 10, _N.NV_COUNTERS),
        NV_COUNTERS_AVAIL = new TPM_PT(TPM_PT.PT_VAR.toInt() + 11, _N.NV_COUNTERS_AVAIL),
        ALGORITHM_SET = new TPM_PT(TPM_PT.PT_VAR.toInt() + 12, _N.ALGORITHM_SET),
        LOADED_CURVES = new TPM_PT(TPM_PT.PT_VAR.toInt() + 13, _N.LOADED_CURVES),
        LOCKOUT_COUNTER = new TPM_PT(TPM_PT.PT_VAR.toInt() + 14, _N.LOCKOUT_COUNTER),
        MAX_AUTH_FAIL = new TPM_PT(TPM_PT.PT_VAR.toInt() + 15, _N.MAX_AUTH_FAIL),
        LOCKOUT_INTERVAL = new TPM_PT(TPM_PT.PT_VAR.toInt() + 16, _N.LOCKOUT_INTERVAL),
        LOCKOUT_RECOVERY = new TPM_PT(TPM_PT.PT_VAR.toInt() + 17, _N.LOCKOUT_RECOVERY),
        NV_WRITE_RECOVERY = new TPM_PT(TPM_PT.PT_VAR.toInt() + 18, _N.NV_WRITE_RECOVERY),
        AUDIT_COUNTER_0 = new TPM_PT(TPM_PT.PT_VAR.toInt() + 19, _N.AUDIT_COUNTER_0),
        AUDIT_COUNTER_1 = new TPM_PT(TPM_PT.PT_VAR.toInt() + 20, _N.AUDIT_COUNTER_1);
    public TPM_PT (int value) { super(value, _ValueMap); }
    
    public static TPM_PT fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_PT.class); }
    
    public static TPM_PT fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_PT.class); }
    
    public static TPM_PT fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_PT.class); }
    
    public TPM_PT._N asEnum() { return (TPM_PT._N)NameAsEnum; }
    
    public static Collection<TPM_PT> values() { return _ValueMap.values(); }
    
    private TPM_PT (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_PT (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

