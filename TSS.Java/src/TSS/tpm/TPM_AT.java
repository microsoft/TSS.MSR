package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* These constants are used in TPM2_AC_GetCapability() to indicate the first tagged value returned from an attached component.
*/
public final class TPM_AT extends TpmEnum<TPM_AT>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_AT. qualifier.
    public enum _N {
        /**
        * in a command, a non-specific request for AC information; in a response, indicates that outputData is not meaningful
        */
        ANY,
        
        /**
        * indicates a TCG defined, device-specific error
        */
        ERROR,
        
        /**
        * indicates the most significant 32 bits of a pairing value for the AC
        */
        PV1,
        
        /**
        * value added to a TPM_AT to indicate a vendor-specific tag value
        */
        VEND
        
    }
    
    private static ValueMap<TPM_AT> _ValueMap = new ValueMap<TPM_AT>();
    
    public static final TPM_AT
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        ANY = new TPM_AT(0x00000000, _N.ANY),
        ERROR = new TPM_AT(0x00000001, _N.ERROR),
        PV1 = new TPM_AT(0x00000002, _N.PV1),
        VEND = new TPM_AT(0x80000000, _N.VEND);
    public TPM_AT (int value) { super(value, _ValueMap); }
    
    public static TPM_AT fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_AT.class); }
    
    public static TPM_AT fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_AT.class); }
    
    public static TPM_AT fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_AT.class); }
    
    public TPM_AT._N asEnum() { return (TPM_AT._N)NameAsEnum; }
    
    public static Collection<TPM_AT> values() { return _ValueMap.values(); }
    
    private TPM_AT (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_AT (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

