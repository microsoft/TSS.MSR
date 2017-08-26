package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This constant value differentiates TPM-generated structures from non-TPM structures.
*/
public final class TPM_GENERATED extends TpmEnum<TPM_GENERATED>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_GENERATED. qualifier.
    public enum _N {
        /**
        * 0xFF TCG (FF 54 43 4716)
        */
        VALUE
        
    }
    
    private static ValueMap<TPM_GENERATED> _ValueMap = new ValueMap<TPM_GENERATED>();
    
    public static final TPM_GENERATED
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        VALUE = new TPM_GENERATED(0xff544347, _N.VALUE);
    public TPM_GENERATED (int value) { super(value, _ValueMap); }
    
    public static TPM_GENERATED fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_GENERATED.class); }
    
    public static TPM_GENERATED fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_GENERATED.class); }
    
    public static TPM_GENERATED fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_GENERATED.class); }
    
    public TPM_GENERATED._N asEnum() { return (TPM_GENERATED._N)NameAsEnum; }
    
    public static Collection<TPM_GENERATED> values() { return _ValueMap.values(); }
    
    private TPM_GENERATED (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_GENERATED (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

