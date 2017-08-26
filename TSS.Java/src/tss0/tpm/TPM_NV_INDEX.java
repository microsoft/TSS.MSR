package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* A TPM_NV_INDEX is used to reference a defined location in NV memory. The format of the Index is changed from TPM 1.2 in order to include the Index in the reserved handle space. Handles in this range use the digest of the public area of the Index as the Name of the entity in authorization computations
*/
public final class TPM_NV_INDEX extends TpmAttribute<TPM_NV_INDEX>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_NV_INDEX. qualifier.
    public enum _N {
        /**
        * The Index of the NV location
        */
        index_BIT_0,
        index_BIT_1,
        index_BIT_2,
        index_BIT_3,
        index_BIT_4,
        index_BIT_5,
        index_BIT_6,
        index_BIT_7,
        index_BIT_8,
        index_BIT_9,
        index_BIT_10,
        index_BIT_11,
        index_BIT_12,
        index_BIT_13,
        index_BIT_14,
        index_BIT_15,
        index_BIT_16,
        index_BIT_17,
        index_BIT_18,
        index_BIT_19,
        index_BIT_20,
        index_BIT_21,
        index_BIT_22,
        index_BIT_23,
        /**
        * constant value of TPM_HT_NV_INDEX indicating the NV Index range
        */
        RhNv_BIT_0,
        RhNv_BIT_1,
        RhNv_BIT_2,
        RhNv_BIT_3,
        RhNv_BIT_4,
        RhNv_BIT_5,
        RhNv_BIT_6,
        RhNv_BIT_7
    }
    
    private static ValueMap<TPM_NV_INDEX>	_ValueMap = new ValueMap<TPM_NV_INDEX>();
    
    public static final TPM_NV_INDEX
    
        index_BIT_0 = new TPM_NV_INDEX(0x1, _N.index_BIT_0),
        index_BIT_1 = new TPM_NV_INDEX(0x2, _N.index_BIT_1),
        index_BIT_2 = new TPM_NV_INDEX(0x4, _N.index_BIT_2),
        index_BIT_3 = new TPM_NV_INDEX(0x8, _N.index_BIT_3),
        index_BIT_4 = new TPM_NV_INDEX(0x10, _N.index_BIT_4),
        index_BIT_5 = new TPM_NV_INDEX(0x20, _N.index_BIT_5),
        index_BIT_6 = new TPM_NV_INDEX(0x40, _N.index_BIT_6),
        index_BIT_7 = new TPM_NV_INDEX(0x80, _N.index_BIT_7),
        index_BIT_8 = new TPM_NV_INDEX(0x100, _N.index_BIT_8),
        index_BIT_9 = new TPM_NV_INDEX(0x200, _N.index_BIT_9),
        index_BIT_10 = new TPM_NV_INDEX(0x400, _N.index_BIT_10),
        index_BIT_11 = new TPM_NV_INDEX(0x800, _N.index_BIT_11),
        index_BIT_12 = new TPM_NV_INDEX(0x1000, _N.index_BIT_12),
        index_BIT_13 = new TPM_NV_INDEX(0x2000, _N.index_BIT_13),
        index_BIT_14 = new TPM_NV_INDEX(0x4000, _N.index_BIT_14),
        index_BIT_15 = new TPM_NV_INDEX(0x8000, _N.index_BIT_15),
        index_BIT_16 = new TPM_NV_INDEX(0x10000, _N.index_BIT_16),
        index_BIT_17 = new TPM_NV_INDEX(0x20000, _N.index_BIT_17),
        index_BIT_18 = new TPM_NV_INDEX(0x40000, _N.index_BIT_18),
        index_BIT_19 = new TPM_NV_INDEX(0x80000, _N.index_BIT_19),
        index_BIT_20 = new TPM_NV_INDEX(0x100000, _N.index_BIT_20),
        index_BIT_21 = new TPM_NV_INDEX(0x200000, _N.index_BIT_21),
        index_BIT_22 = new TPM_NV_INDEX(0x400000, _N.index_BIT_22),
        index_BIT_23 = new TPM_NV_INDEX(0x800000, _N.index_BIT_23),
        RhNv_BIT_0 = new TPM_NV_INDEX(0x1000000, _N.RhNv_BIT_0),
        RhNv_BIT_1 = new TPM_NV_INDEX(0x2000000, _N.RhNv_BIT_1),
        RhNv_BIT_2 = new TPM_NV_INDEX(0x4000000, _N.RhNv_BIT_2),
        RhNv_BIT_3 = new TPM_NV_INDEX(0x8000000, _N.RhNv_BIT_3),
        RhNv_BIT_4 = new TPM_NV_INDEX(0x10000000, _N.RhNv_BIT_4),
        RhNv_BIT_5 = new TPM_NV_INDEX(0x20000000, _N.RhNv_BIT_5),
        RhNv_BIT_6 = new TPM_NV_INDEX(0x40000000, _N.RhNv_BIT_6),
        RhNv_BIT_7 = new TPM_NV_INDEX(0x80000000, _N.RhNv_BIT_7);
    public TPM_NV_INDEX (int value) { super(value, _ValueMap); }
    
    public TPM_NV_INDEX (TPM_NV_INDEX...attrs) { super(_ValueMap, attrs); }
    
    public static TPM_NV_INDEX fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_NV_INDEX.class); }
    
    public static TPM_NV_INDEX fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_NV_INDEX.class); }
    
    public static TPM_NV_INDEX fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_NV_INDEX.class); }
    
    public TPM_NV_INDEX._N asEnum() { return (TPM_NV_INDEX._N)NameAsEnum; }
    
    public static Collection<TPM_NV_INDEX> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPM_NV_INDEX attr) { return super.hasAttr(attr); }
    
    public TPM_NV_INDEX maskAttr (TPM_NV_INDEX attr) { return super.maskAttr(attr, _ValueMap, TPM_NV_INDEX.class); }
    
    private TPM_NV_INDEX (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_NV_INDEX (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

