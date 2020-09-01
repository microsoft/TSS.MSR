package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>

/** A TPM_NV_INDEX is used to reference a defined location in NV memory. The format of the
 *  Index is changed from TPM 1.2 in order to include the Index in the reserved handle
 *  space. Handles in this range use the digest of the public area of the Index as the
 *  Name of the entity in authorization computations
 */
public final class TPM_NV_INDEX extends TpmAttribute<TPM_NV_INDEX>
{
    /** Values from enum _N are only intended to be used in case labels of a switch statement
     *  using the result of this.asEnum() method as the switch condition. However, their Java
     *  names are identical to those of the constants defined in this class further below, so
     *  for any other usage just prepend them with the TPM_NV_INDEX. qualifier.
     */
    public enum _N {
        /** The Index of the NV location  */
        index_BIT_MASK,
        index_BIT_OFFSET,
        index_BIT_LENGTH,

        /** Constant value of TPM_HT_NV_INDEX indicating the NV Index range  */
        RhNv_BIT_MASK,
        RhNv_BIT_OFFSET,
        RhNv_BIT_LENGTH
    }

    private static ValueMap<TPM_NV_INDEX> _ValueMap = new ValueMap<TPM_NV_INDEX>();

    /** These definitions provide mapping of the Java enum constants to their TPM integer values  */
    public static final TPM_NV_INDEX
        index_BIT_MASK = new TPM_NV_INDEX(0xFFFFFF, _N.index_BIT_MASK),
        index_BIT_OFFSET = new TPM_NV_INDEX(0, _N.index_BIT_OFFSET),
        index_BIT_LENGTH = new TPM_NV_INDEX(24, _N.index_BIT_LENGTH),
        RhNv_BIT_MASK = new TPM_NV_INDEX(0xFF000000, _N.RhNv_BIT_MASK),
        RhNv_BIT_OFFSET = new TPM_NV_INDEX(24, _N.RhNv_BIT_OFFSET),
        RhNv_BIT_LENGTH = new TPM_NV_INDEX(8, _N.RhNv_BIT_LENGTH);

    public TPM_NV_INDEX () { super(0, _ValueMap); }
    public TPM_NV_INDEX (int value) { super(value, _ValueMap); }
    public TPM_NV_INDEX (TPM_NV_INDEX...attrs) { super(_ValueMap, attrs); }
    public static TPM_NV_INDEX fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_NV_INDEX.class); }
    public static TPM_NV_INDEX fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_NV_INDEX.class); }
    public static TPM_NV_INDEX fromTpm (TpmBuffer buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_NV_INDEX.class); }
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
