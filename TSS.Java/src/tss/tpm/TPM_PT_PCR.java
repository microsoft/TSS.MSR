package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* The TPM_PT_PCR constants are used in TPM2_GetCapability() to indicate the property being selected or returned. The PCR properties can be read when capability == TPM_CAP_PCR_PROPERTIES. If there is no property that corresponds to the value of property, the next higher value is returned, if it exists.
*/
public final class TPM_PT_PCR extends TpmEnum<TPM_PT_PCR>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_PT_PCR. qualifier.
    public enum _N {
        /**
        * bottom of the range of TPM_PT_PCR properties
        */
        FIRST,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR is saved and restored by TPM_SU_STATE
        */
        SAVE,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be extended from locality 0 This property is only present if a locality other than 0 is implemented.
        */
        EXTEND_L0,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be reset by TPM2_PCR_Reset() from locality 0
        */
        RESET_L0,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be extended from locality 1 This property is only present if locality 1 is implemented.
        */
        EXTEND_L1,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be reset by TPM2_PCR_Reset() from locality 1 This property is only present if locality 1 is implemented.
        */
        RESET_L1,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be extended from locality 2 This property is only present if localities 1 and 2 are implemented.
        */
        EXTEND_L2,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be reset by TPM2_PCR_Reset() from locality 2 This property is only present if localities 1 and 2 are implemented.
        */
        RESET_L2,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be extended from locality 3 This property is only present if localities 1, 2, and 3 are implemented.
        */
        EXTEND_L3,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be reset by TPM2_PCR_Reset() from locality 3 This property is only present if localities 1, 2, and 3 are implemented.
        */
        RESET_L3,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be extended from locality 4 This property is only present if localities 1, 2, 3, and 4 are implemented.
        */
        EXTEND_L4,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be reset by TPM2_PCR_Reset() from locality 4 This property is only present if localities 1, 2, 3, and 4 are implemented.
        */
        RESET_L4,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that modifications to this PCR (reset or Extend) will not increment the pcrUpdateCounter
        */
        NO_INCREMENT,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR is reset by a D-RTM event These PCR are reset to -1 on TPM2_Startup() and reset to 0 on a _TPM_Hash_End event following a _TPM_Hash_Start event.
        */
        DRTM_RESET,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR is controlled by policy This property is only present if the TPM supports policy control of a PCR.
        */
        POLICY,
        
        /**
        * a SET bit in the TPMS_PCR_SELECT indicates that the PCR is controlled by an authorization value This property is only present if the TPM supports authorization control of a PCR.
        */
        AUTH,
        
        /**
        * top of the range of TPM_PT_PCR properties of the implementation If the TPM receives a request for a PCR property with a value larger than this, the TPM will return a zero length list and set the moreData parameter to NO. NOTE This is an implementation-specific value. The value shown reflects the reference code implementation.
        */
        LAST
        
    }
    
    private static ValueMap<TPM_PT_PCR> _ValueMap = new ValueMap<TPM_PT_PCR>();
    
    public static final TPM_PT_PCR
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        FIRST = new TPM_PT_PCR(0x00000000, _N.FIRST),
        SAVE = new TPM_PT_PCR(0x00000000, _N.SAVE),
        EXTEND_L0 = new TPM_PT_PCR(0x00000001, _N.EXTEND_L0),
        RESET_L0 = new TPM_PT_PCR(0x00000002, _N.RESET_L0),
        EXTEND_L1 = new TPM_PT_PCR(0x00000003, _N.EXTEND_L1),
        RESET_L1 = new TPM_PT_PCR(0x00000004, _N.RESET_L1),
        EXTEND_L2 = new TPM_PT_PCR(0x00000005, _N.EXTEND_L2),
        RESET_L2 = new TPM_PT_PCR(0x00000006, _N.RESET_L2),
        EXTEND_L3 = new TPM_PT_PCR(0x00000007, _N.EXTEND_L3),
        RESET_L3 = new TPM_PT_PCR(0x00000008, _N.RESET_L3),
        EXTEND_L4 = new TPM_PT_PCR(0x00000009, _N.EXTEND_L4),
        RESET_L4 = new TPM_PT_PCR(0x0000000A, _N.RESET_L4),
        NO_INCREMENT = new TPM_PT_PCR(0x00000011, _N.NO_INCREMENT),
        DRTM_RESET = new TPM_PT_PCR(0x00000012, _N.DRTM_RESET),
        POLICY = new TPM_PT_PCR(0x00000013, _N.POLICY),
        AUTH = new TPM_PT_PCR(0x00000014, _N.AUTH),
        LAST = new TPM_PT_PCR(0x00000014, _N.LAST);
    public TPM_PT_PCR (int value) { super(value, _ValueMap); }
    
    public static TPM_PT_PCR fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_PT_PCR.class); }
    
    public static TPM_PT_PCR fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_PT_PCR.class); }
    
    public static TPM_PT_PCR fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_PT_PCR.class); }
    
    public TPM_PT_PCR._N asEnum() { return (TPM_PT_PCR._N)NameAsEnum; }
    
    public static Collection<TPM_PT_PCR> values() { return _ValueMap.values(); }
    
    private TPM_PT_PCR (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_PT_PCR (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

