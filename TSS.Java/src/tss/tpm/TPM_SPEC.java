package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>

/** These values are readable with TPM2_GetCapability() (see 6.13 for the format). */
public final class TPM_SPEC extends TpmEnum<TPM_SPEC>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_SPEC. qualifier.
    public enum _N {
        /** ASCII 2.0 with null terminator */
        FAMILY,
        
        /** the level number for the specification */
        LEVEL,
        
        /** the version number of the spec (001.62 * 100) */
        VERSION,
        
        /** the year of the version */
        YEAR,
        
        /** the day of the year (December 26) */
        DAY_OF_YEAR
    }

    private static ValueMap<TPM_SPEC> _ValueMap = new ValueMap<TPM_SPEC>();
    
    /** These definitions provide mapping of the Java enum constants to their TPM integer values */
    public static final TPM_SPEC
        FAMILY = new TPM_SPEC(0x322E3000, _N.FAMILY),
        LEVEL = new TPM_SPEC(0, _N.LEVEL),
        VERSION = new TPM_SPEC(162, _N.VERSION),
        YEAR = new TPM_SPEC(2019, _N.YEAR),
        DAY_OF_YEAR = new TPM_SPEC(360, _N.DAY_OF_YEAR);
    
    public TPM_SPEC (int value) { super(value, _ValueMap); }
    
    public static TPM_SPEC fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_SPEC.class); }
    
    public static TPM_SPEC fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_SPEC.class); }
    
    public static TPM_SPEC fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_SPEC.class); }
    
    public TPM_SPEC._N asEnum() { return (TPM_SPEC._N)NameAsEnum; }
    
    public static Collection<TPM_SPEC> values() { return _ValueMap.values(); }
    
    private TPM_SPEC (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_SPEC (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

