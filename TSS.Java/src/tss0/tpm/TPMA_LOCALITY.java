package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* In a TPMS_CREATION_DATA structure, this structure is used to indicate the locality of the command that created the object. No more than one of the locality attributes shall be set in the creation data.
*/
public final class TPMA_LOCALITY extends TpmAttribute<TPMA_LOCALITY>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_LOCALITY. qualifier.
    public enum _N {
        LOC_ZERO,
        LOC_ONE,
        LOC_TWO,
        LOC_THREE,
        LOC_FOUR,
        /**
        * If any of these bits is set, an extended locality is indicated
        */
        Extended_BIT_0,
        Extended_BIT_1,
        Extended_BIT_2
    }
    
    private static ValueMap<TPMA_LOCALITY>	_ValueMap = new ValueMap<TPMA_LOCALITY>();
    
    public static final TPMA_LOCALITY
    
        LOC_ZERO = new TPMA_LOCALITY(0x1, _N.LOC_ZERO),
        LOC_ONE = new TPMA_LOCALITY(0x2, _N.LOC_ONE),
        LOC_TWO = new TPMA_LOCALITY(0x4, _N.LOC_TWO),
        LOC_THREE = new TPMA_LOCALITY(0x8, _N.LOC_THREE),
        LOC_FOUR = new TPMA_LOCALITY(0x10, _N.LOC_FOUR),
        Extended_BIT_0 = new TPMA_LOCALITY(0x20, _N.Extended_BIT_0),
        Extended_BIT_1 = new TPMA_LOCALITY(0x40, _N.Extended_BIT_1),
        Extended_BIT_2 = new TPMA_LOCALITY(0x80, _N.Extended_BIT_2);
    public TPMA_LOCALITY (int value) { super(value, _ValueMap); }
    
    public TPMA_LOCALITY (TPMA_LOCALITY...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_LOCALITY fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_LOCALITY.class); }
    
    public static TPMA_LOCALITY fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_LOCALITY.class); }
    
    public static TPMA_LOCALITY fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_LOCALITY.class); }
    
    public TPMA_LOCALITY._N asEnum() { return (TPMA_LOCALITY._N)NameAsEnum; }
    
    public static Collection<TPMA_LOCALITY> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_LOCALITY attr) { return super.hasAttr(attr); }
    
    public TPMA_LOCALITY maskAttr (TPMA_LOCALITY attr) { return super.maskAttr(attr, _ValueMap, TPMA_LOCALITY.class); }
    
    private TPMA_LOCALITY (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_LOCALITY (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 1; }
}

//<<<

