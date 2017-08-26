package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This table contains a collection of values used in various parts of the reference code. The values shown are illustrative.
*/
public final class Implementation extends TpmEnum<Implementation>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the Implementation. qualifier.
    public enum _N {
        /**
        * temporary define
        */
        FIELD_UPGRADE_IMPLEMENTED,
        
        /**
        * The number of bits in a word. This is used in the big number library to set the unit of allocation. The reference implementation allows values of 32 and 64. It should be set to a value that is compatible with libraries that are used (e.g, OpenSSL).
        */
        RADIX_BITS,
        
        /**
        * The byte alignment for hash structure
        */
        HASH_ALIGNMENT,
        
        /**
        * The alignment for symmetric structures.
        */
        SYMMETRIC_ALIGNMENT,
        
        /**
        * Selection of the library that provides the basic hashing functions.
        */
        HASH_LIB,
        
        /**
        * Selection of the library that provides the low-level symmetric cryptography. Choices are determined by the vendor (See LibSupport.h for implications).
        */
        SYM_LIB,
        
        /**
        * Selection of the library that provides the big number math including ECC. Choices are determined by the vendor (See LibSupport.h for implications).
        */
        MATH_LIB,
        
        /**
        * the number of PCR in the TPM
        */
        IMPLEMENTATION_PCR,
        
        /**
        * the number of PCR required by the relevant platform specification
        */
        PLATFORM_PCR,
        
        /**
        * the D-RTM PCR NOTE This value is not defined when the TPM does not implement D-RTM
        */
        DRTM_PCR,
        
        /**
        * the PCR that will receive the H-CRTM value at TPM2_Startup. This value should not be changed.
        */
        HCRTM_PCR,
        
        /**
        * the number of localities supported by the TPM This is expected to be either 5 for a PC, or 1 for just about everything else.
        */
        NUM_LOCALITIES,
        
        /**
        * the maximum number of handles in the handle area This should be produced by the Part 3 parser but is here for now.
        */
        MAX_HANDLE_NUM,
        
        /**
        * the number of simultaneously active sessions that are supported by the TPM implementation
        */
        MAX_ACTIVE_SESSIONS,
        
        /**
        * the number of sessions that the TPM may have in memory
        */
        MAX_LOADED_SESSIONS,
        
        /**
        * this is the current maximum value
        */
        MAX_SESSION_NUM,
        
        /**
        * the number of simultaneously loaded objects that are supported by the TPM; this number does not include the objects that may be placed in NV memory by TPM2_EvictControl().
        */
        MAX_LOADED_OBJECTS,
        
        /**
        * the minimum number of evict objects supported by the TPM
        */
        MIN_EVICT_OBJECTS,
        
        PCR_SELECT_MIN,
        
        PCR_SELECT_MAX,
        
        /**
        * number of PCR groups that have individual policies
        */
        NUM_POLICY_PCR_GROUP,
        
        /**
        * number of PCR groups that have individual authorization values
        */
        NUM_AUTHVALUE_PCR_GROUP,
        
        MAX_CONTEXT_SIZE,
        
        MAX_DIGEST_BUFFER,
        
        /**
        * maximum data size allowed in an NV Index
        */
        MAX_NV_INDEX_SIZE,
        
        /**
        * maximum data size in one NV read or write command
        */
        MAX_NV_BUFFER_SIZE,
        
        /**
        * maximum size of a capability buffer
        */
        MAX_CAP_BUFFER,
        
        /**
        * size of NV memory in octets
        */
        NV_MEMORY_SIZE,
        
        /**
        * the TPM will not allocate a non-counter index if it would prevent allocation of this number of indices.
        */
        MIN_COUNTER_INDICES,
        
        NUM_STATIC_PCR,
        
        /**
        * number of algorithms that can be in a list
        */
        MAX_ALG_LIST_SIZE,
        
        /**
        * size of the Primary Seed in octets
        */
        PRIMARY_SEED_SIZE,
        
        /**
        * context encryption algorithm Just use the root so that the macros in GpMacros.h will work correctly.
        */
        CONTEXT_ENCRYPT_ALGORITHM,
        
        /**
        * the update interval expressed as a power of 2 seconds A value of 12 is 4,096 seconds (~68 minutes).
        */
        NV_CLOCK_UPDATE_INTERVAL,
        
        /**
        * number of PCR groups that allow policy/auth
        */
        NUM_POLICY_PCR,
        
        /**
        * maximum size of a command
        */
        MAX_COMMAND_SIZE,
        
        /**
        * maximum size of a response
        */
        MAX_RESPONSE_SIZE,
        
        /**
        * number between 1 and 32 inclusive
        */
        ORDERLY_BITS,
        
        /**
        * the maximum number of octets that may be in a sealed blob; 128 is the minimum allowed value
        */
        MAX_SYM_DATA,
        
        MAX_RNG_ENTROPY_SIZE,
        
        /**
        * Number of bytes used for the RAM index space. If this is not large enough, it might not be possible to allocate orderly indices.
        */
        RAM_INDEX_SPACE,
        
        /**
        * 216 + 1
        */
        RSA_DEFAULT_PUBLIC_EXPONENT,
        
        /**
        * indicates if the TPM_PT_PCR_NO_INCREMENT group is implemented
        */
        ENABLE_PCR_NO_INCREMENT,
        
        CRT_FORMAT_RSA,
        
        VENDOR_COMMAND_COUNT,
        
        /**
        * MAX_RSA_KEY_BYTES is auto generated from the RSA key size selection in Table 4. If RSA is not implemented, this may need to be manually removed.
        */
        PRIVATE_VENDOR_SPECIFIC_BYTES,
        
        /**
        * Maximum size of the vendor-specific buffer
        */
        MAX_VENDOR_BUFFER_SIZE
        
    }
    
    private static ValueMap<Implementation> _ValueMap = new ValueMap<Implementation>();
    
    public static final Implementation
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        FIELD_UPGRADE_IMPLEMENTED = new Implementation(Logic.NO.toInt(), _N.FIELD_UPGRADE_IMPLEMENTED),
        RADIX_BITS = new Implementation(32, _N.RADIX_BITS),
        HASH_ALIGNMENT = new Implementation(4, _N.HASH_ALIGNMENT),
        SYMMETRIC_ALIGNMENT = new Implementation(4, _N.SYMMETRIC_ALIGNMENT),
        HASH_LIB = new Implementation(ImplementationConstants.OSSL.toInt(), _N.HASH_LIB),
        SYM_LIB = new Implementation(ImplementationConstants.OSSL.toInt(), _N.SYM_LIB),
        MATH_LIB = new Implementation(ImplementationConstants.OSSL.toInt(), _N.MATH_LIB),
        IMPLEMENTATION_PCR = new Implementation(24, _N.IMPLEMENTATION_PCR),
        PLATFORM_PCR = new Implementation(24, _N.PLATFORM_PCR),
        DRTM_PCR = new Implementation(17, _N.DRTM_PCR),
        HCRTM_PCR = new Implementation(0, _N.HCRTM_PCR),
        NUM_LOCALITIES = new Implementation(5, _N.NUM_LOCALITIES),
        MAX_HANDLE_NUM = new Implementation(3, _N.MAX_HANDLE_NUM),
        MAX_ACTIVE_SESSIONS = new Implementation(64, _N.MAX_ACTIVE_SESSIONS),
        MAX_LOADED_SESSIONS = new Implementation(3, _N.MAX_LOADED_SESSIONS),
        MAX_SESSION_NUM = new Implementation(3, _N.MAX_SESSION_NUM),
        MAX_LOADED_OBJECTS = new Implementation(3, _N.MAX_LOADED_OBJECTS),
        MIN_EVICT_OBJECTS = new Implementation(2, _N.MIN_EVICT_OBJECTS),
        PCR_SELECT_MIN = new Implementation(((Implementation.PLATFORM_PCR.toInt()+7)/8), _N.PCR_SELECT_MIN),
        PCR_SELECT_MAX = new Implementation(((Implementation.IMPLEMENTATION_PCR.toInt()+7)/8), _N.PCR_SELECT_MAX),
        NUM_POLICY_PCR_GROUP = new Implementation(1, _N.NUM_POLICY_PCR_GROUP),
        NUM_AUTHVALUE_PCR_GROUP = new Implementation(1, _N.NUM_AUTHVALUE_PCR_GROUP),
        MAX_CONTEXT_SIZE = new Implementation(2474, _N.MAX_CONTEXT_SIZE),
        MAX_DIGEST_BUFFER = new Implementation(1024, _N.MAX_DIGEST_BUFFER),
        MAX_NV_INDEX_SIZE = new Implementation(2048, _N.MAX_NV_INDEX_SIZE),
        MAX_NV_BUFFER_SIZE = new Implementation(1024, _N.MAX_NV_BUFFER_SIZE),
        MAX_CAP_BUFFER = new Implementation(1024, _N.MAX_CAP_BUFFER),
        NV_MEMORY_SIZE = new Implementation(16384, _N.NV_MEMORY_SIZE),
        MIN_COUNTER_INDICES = new Implementation(8, _N.MIN_COUNTER_INDICES),
        NUM_STATIC_PCR = new Implementation(16, _N.NUM_STATIC_PCR),
        MAX_ALG_LIST_SIZE = new Implementation(64, _N.MAX_ALG_LIST_SIZE),
        PRIMARY_SEED_SIZE = new Implementation(32, _N.PRIMARY_SEED_SIZE),
        CONTEXT_ENCRYPT_ALGORITHM = new Implementation(TPM_ALG_ID.AES.toInt(), _N.CONTEXT_ENCRYPT_ALGORITHM),
        NV_CLOCK_UPDATE_INTERVAL = new Implementation(12, _N.NV_CLOCK_UPDATE_INTERVAL),
        NUM_POLICY_PCR = new Implementation(1, _N.NUM_POLICY_PCR),
        MAX_COMMAND_SIZE = new Implementation(4096, _N.MAX_COMMAND_SIZE),
        MAX_RESPONSE_SIZE = new Implementation(4096, _N.MAX_RESPONSE_SIZE),
        ORDERLY_BITS = new Implementation(8, _N.ORDERLY_BITS),
        MAX_SYM_DATA = new Implementation(128, _N.MAX_SYM_DATA),
        MAX_RNG_ENTROPY_SIZE = new Implementation(64, _N.MAX_RNG_ENTROPY_SIZE),
        RAM_INDEX_SPACE = new Implementation(512, _N.RAM_INDEX_SPACE),
        RSA_DEFAULT_PUBLIC_EXPONENT = new Implementation(0x00010001, _N.RSA_DEFAULT_PUBLIC_EXPONENT),
        ENABLE_PCR_NO_INCREMENT = new Implementation(Logic.YES.toInt(), _N.ENABLE_PCR_NO_INCREMENT),
        CRT_FORMAT_RSA = new Implementation(Logic.YES.toInt(), _N.CRT_FORMAT_RSA),
        VENDOR_COMMAND_COUNT = new Implementation(0, _N.VENDOR_COMMAND_COUNT),
        PRIVATE_VENDOR_SPECIFIC_BYTES = new Implementation(((ImplementationConstants.MAX_RSA_KEY_BYTES.toInt()/2) * (3 + Implementation.CRT_FORMAT_RSA.toInt() * 2)), _N.PRIVATE_VENDOR_SPECIFIC_BYTES),
        MAX_VENDOR_BUFFER_SIZE = new Implementation(1024, _N.MAX_VENDOR_BUFFER_SIZE);
    public Implementation (int value) { super(value, _ValueMap); }
    
    public static Implementation fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, Implementation.class); }
    
    public static Implementation fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, Implementation.class); }
    
    public static Implementation fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, Implementation.class); }
    
    public Implementation._N asEnum() { return (Implementation._N)NameAsEnum; }
    
    public static Collection<Implementation> values() { return _ValueMap.values(); }
    
    private Implementation (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private Implementation (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

