package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* Structure tags are used to disambiguate structures. They are 16-bit values with the most significant bit SET so that they do not overlap TPM_ALG_ID values. A single exception is made for the value associated with TPM_ST_RSP_COMMAND (0x00C4), which has the same value as the TPM_TAG_RSP_COMMAND tag from earlier versions of this specification. This value is used when the TPM is compatible with a previous TPM specification and the TPM cannot determine which family of response code to return because the command tag is not valid.
*/
public final class TPM_ST extends TpmEnum<TPM_ST>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_ST. qualifier.
    public enum _N {
        /**
        * tag value for a response; used when there is an error in the tag. This is also the value returned from a TPM 1.2 when an error occurs. This value is used in this specification because an error in the command tag may prevent determination of the family. When this tag is used in the response, the response code will be TPM_RC_BAD_TAG (0 1E16), which has the same numeric value as the TPM 1.2 response code for TPM_BADTAG. NOTE In a previously published version of this specification, TPM_RC_BAD_TAG was incorrectly assigned a value of 0x030 instead of 30 (0x01e). Some implementations my return the old value instead of the new value.
        */
        RSP_COMMAND,
        
        /**
        * no structure type specified
        */
        NULL,
        
        /**
        * tag value for a command/response for a command defined in this specification; indicating that the command/response has no attached sessions and no authorizationSize/parameterSize value is present If the responseCode from the TPM is not TPM_RC_SUCCESS, then the response tag shall have this value.
        */
        NO_SESSIONS,
        
        /**
        * tag value for a command/response for a command defined in this specification; indicating that the command/response has one or more attached sessions and the authorizationSize/parameterSize field is present
        */
        SESSIONS,
        
        /**
        * tag for an attestation structure
        */
        ATTEST_NV,
        
        /**
        * tag for an attestation structure
        */
        ATTEST_COMMAND_AUDIT,
        
        /**
        * tag for an attestation structure
        */
        ATTEST_SESSION_AUDIT,
        
        /**
        * tag for an attestation structure
        */
        ATTEST_CERTIFY,
        
        /**
        * tag for an attestation structure
        */
        ATTEST_QUOTE,
        
        /**
        * tag for an attestation structure
        */
        ATTEST_TIME,
        
        /**
        * tag for an attestation structure
        */
        ATTEST_CREATION,
        
        /**
        * tag for a ticket type
        */
        CREATION,
        
        /**
        * tag for a ticket type
        */
        VERIFIED,
        
        /**
        * tag for a ticket type
        */
        AUTH_SECRET,
        
        /**
        * tag for a ticket type
        */
        HASHCHECK,
        
        /**
        * tag for a ticket type
        */
        AUTH_SIGNED,
        
        /**
        * tag for a structure describing a Field Upgrade Policy
        */
        FU_MANIFEST
        
    }
    
    private static ValueMap<TPM_ST> _ValueMap = new ValueMap<TPM_ST>();
    
    public static final TPM_ST
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        RSP_COMMAND = new TPM_ST(0x00C4, _N.RSP_COMMAND),
        NULL = new TPM_ST(0X8000, _N.NULL),
        NO_SESSIONS = new TPM_ST(0x8001, _N.NO_SESSIONS),
        SESSIONS = new TPM_ST(0x8002, _N.SESSIONS),
        ATTEST_NV = new TPM_ST(0x8014, _N.ATTEST_NV),
        ATTEST_COMMAND_AUDIT = new TPM_ST(0x8015, _N.ATTEST_COMMAND_AUDIT),
        ATTEST_SESSION_AUDIT = new TPM_ST(0x8016, _N.ATTEST_SESSION_AUDIT),
        ATTEST_CERTIFY = new TPM_ST(0x8017, _N.ATTEST_CERTIFY),
        ATTEST_QUOTE = new TPM_ST(0x8018, _N.ATTEST_QUOTE),
        ATTEST_TIME = new TPM_ST(0x8019, _N.ATTEST_TIME),
        ATTEST_CREATION = new TPM_ST(0x801A, _N.ATTEST_CREATION),
        CREATION = new TPM_ST(0x8021, _N.CREATION),
        VERIFIED = new TPM_ST(0x8022, _N.VERIFIED),
        AUTH_SECRET = new TPM_ST(0x8023, _N.AUTH_SECRET),
        HASHCHECK = new TPM_ST(0x8024, _N.HASHCHECK),
        AUTH_SIGNED = new TPM_ST(0x8025, _N.AUTH_SIGNED),
        FU_MANIFEST = new TPM_ST(0x8029, _N.FU_MANIFEST);
    public TPM_ST (int value) { super(value, _ValueMap); }
    
    public static TPM_ST fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_ST.class); }
    
    public static TPM_ST fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_ST.class); }
    
    public static TPM_ST fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_ST.class); }
    
    public TPM_ST._N asEnum() { return (TPM_ST._N)NameAsEnum; }
    
    public static Collection<TPM_ST> values() { return _ValueMap.values(); }
    
    private TPM_ST (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_ST (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 2; }
}

//<<<

