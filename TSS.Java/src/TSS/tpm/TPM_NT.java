package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This table lists the values of the TPM_NT field of a TPMA_NV. See Table 205 for usage.
*/
public final class TPM_NT extends TpmEnum<TPM_NT>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_NT. qualifier.
    public enum _N {
        /**
        * Ordinary contains data that is opaque to the TPM that can only be modified using TPM2_NV_Write().
        */
        ORDINARY,
        
        /**
        * Counter contains an 8-octet value that is to be used as a counter and can only be modified with TPM2_NV_Increment()
        */
        COUNTER,
        
        /**
        * Bit Field contains an 8-octet value to be used as a bit field and can only be modified with TPM2_NV_SetBits().
        */
        BITS,
        
        /**
        * Extend contains a digest-sized value used like a PCR. The Index can only be modified using TPM2_NV_Extend(). The extend will use the nameAlg of the Index.
        */
        EXTEND,
        
        /**
        * PIN Fail - contains pinCount that increments on a PIN authorization failure and a pinLimit
        */
        PIN_FAIL,
        
        /**
        * PIN Pass - contains pinCount that increments on a PIN authorization success and a pinLimit
        */
        PIN_PASS
        
    }
    
    private static ValueMap<TPM_NT> _ValueMap = new ValueMap<TPM_NT>();
    
    public static final TPM_NT
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        ORDINARY = new TPM_NT(0x0, _N.ORDINARY),
        COUNTER = new TPM_NT(0x1, _N.COUNTER),
        BITS = new TPM_NT(0x2, _N.BITS),
        EXTEND = new TPM_NT(0x4, _N.EXTEND),
        PIN_FAIL = new TPM_NT(0x8, _N.PIN_FAIL),
        PIN_PASS = new TPM_NT(0x9, _N.PIN_PASS);
    public TPM_NT (int value) { super(value, _ValueMap); }
    
    public static TPM_NT fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_NT.class); }
    
    public static TPM_NT fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_NT.class); }
    
    public static TPM_NT fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_NT.class); }
    
    public TPM_NT._N asEnum() { return (TPM_NT._N)NameAsEnum; }
    
    public static Collection<TPM_NT> values() { return _ValueMap.values(); }
    
    private TPM_NT (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_NT (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

