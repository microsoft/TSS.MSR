package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* These values are readable with TPM2_GetCapability(). They are the TPM_PT_PS_xxx values.
*/
public final class PLATFORM extends TpmEnum<PLATFORM>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the PLATFORM. qualifier.
    public enum _N {
        FAMILY,
        
        LEVEL,
        
        VERSION,
        
        YEAR,
        
        DAY_OF_YEAR
        
    }
    
    private static ValueMap<PLATFORM> _ValueMap = new ValueMap<PLATFORM>();
    
    public static final PLATFORM
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        FAMILY = new PLATFORM(TPM_SPEC.FAMILY.toInt(), _N.FAMILY),
        LEVEL = new PLATFORM(TPM_SPEC.LEVEL.toInt(), _N.LEVEL),
        VERSION = new PLATFORM(TPM_SPEC.VERSION.toInt(), _N.VERSION),
        YEAR = new PLATFORM(TPM_SPEC.YEAR.toInt(), _N.YEAR),
        DAY_OF_YEAR = new PLATFORM(TPM_SPEC.DAY_OF_YEAR.toInt(), _N.DAY_OF_YEAR);
    public PLATFORM (int value) { super(value, _ValueMap); }
    
    public static PLATFORM fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, PLATFORM.class); }
    
    public static PLATFORM fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, PLATFORM.class); }
    
    public static PLATFORM fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, PLATFORM.class); }
    
    public PLATFORM._N asEnum() { return (PLATFORM._N)NameAsEnum; }
    
    public static Collection<PLATFORM> values() { return _ValueMap.values(); }
    
    private PLATFORM (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private PLATFORM (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

