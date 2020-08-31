package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>

/** This structure defines the attributes of a command from a context management
 *  perspective. The fields of the structure indicate to the TPM Resource Manager (TRM)
 *  the number of resources required by a command and how the command affects the TPMs resources.
 */
public final class TPMA_CC extends TpmAttribute<TPMA_CC>
{
    /** Values from enum _N are only intended to be used in case labels of a switch statement
     *  using this.asEnum() method in the switch condition. However, their Java names
     *  are identical to those of the constants defined in this class further below,
     *  so for any other usage just prepend them with the 'TPMA_CC.' qualifier.
     */
    public enum _N {
        /** Indicates the command being selected  */
        commandIndex_BIT_MASK,
        commandIndex_BIT_OFFSET,
        commandIndex_BIT_LENGTH,

        /** SET (1): indicates that the command may write to NV
         *  CLEAR (0): indicates that the command does not write to NV
         */
        nv,

        /** SET (1): This command could flush any number of loaded contexts.
         *  CLEAR (0): no additional changes other than indicated by the flushed attribute
         */
        extensive,

        /** SET (1): The context associated with any transient handle in the command will be
         *  flushed when this command completes.
         *  CLEAR (0): No context is flushed as a side effect of this command.
         */
        flushed,

        /** Indicates the number of the handles in the handle area for this command  */
        cHandles_BIT_MASK,
        cHandles_BIT_OFFSET,
        cHandles_BIT_LENGTH,

        /** SET (1): indicates the presence of the handle area in the response  */
        rHandle,

        /** SET (1): indicates that the command is vendor-specific
         *  CLEAR (0): indicates that the command is defined in a version of this specification
         */
        V,

        /** Allocated for software; shall be zero  */
        Res_BIT_MASK,
        Res_BIT_OFFSET,
        Res_BIT_LENGTH
    }

    private static ValueMap<TPMA_CC> _ValueMap = new ValueMap<TPMA_CC>();

    /** These definitions provide mapping of Java enum constants to their TPM integer values  */
    public static final TPMA_CC
        commandIndex_BIT_MASK = new TPMA_CC(0xFFFF, _N.commandIndex_BIT_MASK),
        commandIndex_BIT_OFFSET = new TPMA_CC(0, _N.commandIndex_BIT_OFFSET),
        commandIndex_BIT_LENGTH = new TPMA_CC(16, _N.commandIndex_BIT_LENGTH),
        nv = new TPMA_CC(0x400000, _N.nv),
        extensive = new TPMA_CC(0x800000, _N.extensive),
        flushed = new TPMA_CC(0x1000000, _N.flushed),
        cHandles_BIT_MASK = new TPMA_CC(0xE000000, _N.cHandles_BIT_MASK),
        cHandles_BIT_OFFSET = new TPMA_CC(25, _N.cHandles_BIT_OFFSET),
        cHandles_BIT_LENGTH = new TPMA_CC(3, _N.cHandles_BIT_LENGTH),
        rHandle = new TPMA_CC(0x10000000, _N.rHandle),
        V = new TPMA_CC(0x20000000, _N.V),
        Res_BIT_MASK = new TPMA_CC(0xC0000000, _N.Res_BIT_MASK),
        Res_BIT_OFFSET = new TPMA_CC(30, _N.Res_BIT_OFFSET),
        Res_BIT_LENGTH = new TPMA_CC(2, _N.Res_BIT_LENGTH);

    public TPMA_CC () { super(0, _ValueMap); }
    public TPMA_CC (int value) { super(value, _ValueMap); }
    public TPMA_CC (TPMA_CC...attrs) { super(_ValueMap, attrs); }
    public static TPMA_CC fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_CC.class); }
    public static TPMA_CC fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_CC.class); }
    public static TPMA_CC fromTpm (TpmBuffer buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_CC.class); }
    public TPMA_CC._N asEnum() { return (TPMA_CC._N)NameAsEnum; }
    public static Collection<TPMA_CC> values() { return _ValueMap.values(); }
    public boolean hasAttr (TPMA_CC attr) { return super.hasAttr(attr); }
    public TPMA_CC maskAttr (TPMA_CC attr) { return super.maskAttr(attr, _ValueMap, TPMA_CC.class); }
    private TPMA_CC (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    private TPMA_CC (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }

    @Override
    protected int wireSize() { return 4; }
}

//<<<
