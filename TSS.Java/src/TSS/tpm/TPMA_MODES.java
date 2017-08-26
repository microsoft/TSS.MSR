package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure of this attribute is used to report that the TPM is designed for these modes. This structure may be read using TPM2_GetCapability(capability = TPM_CAP_TPM_PROPERTIES, property = TPM_PT_MODES).
*/
public final class TPMA_MODES extends TpmAttribute<TPMA_MODES>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_MODES. qualifier.
    public enum _N {
        /**
        * SET (1): indicates that the TPM is designed to comply with all of the FIPS 140-2 requirements at Level 1 or higher.
        */
        FIPS_140_2
    }
    
    private static ValueMap<TPMA_MODES>	_ValueMap = new ValueMap<TPMA_MODES>();
    
    public static final TPMA_MODES
    
        FIPS_140_2 = new TPMA_MODES(0x1, _N.FIPS_140_2);
    public TPMA_MODES (int value) { super(value, _ValueMap); }
    
    public TPMA_MODES (TPMA_MODES...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_MODES fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_MODES.class); }
    
    public static TPMA_MODES fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_MODES.class); }
    
    public static TPMA_MODES fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_MODES.class); }
    
    public TPMA_MODES._N asEnum() { return (TPMA_MODES._N)NameAsEnum; }
    
    public static Collection<TPMA_MODES> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_MODES attr) { return super.hasAttr(attr); }
    
    public TPMA_MODES maskAttr (TPMA_MODES attr) { return super.maskAttr(attr, _ValueMap, TPMA_MODES.class); }
    
    private TPMA_MODES (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_MODES (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

