package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure defines the attributes of a command from a context management perspective. The fields of the structure indicate to the TPM Resource Manager (TRM) the number of resources required by a command and how the command affects the TPMs resources.
*/
public final class TPMA_CC extends TpmAttribute<TPMA_CC>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_CC. qualifier.
    public enum _N {
        /**
        * indicates the command being selected
        */
        commandIndex_BIT_0,
        commandIndex_BIT_1,
        commandIndex_BIT_2,
        commandIndex_BIT_3,
        commandIndex_BIT_4,
        commandIndex_BIT_5,
        commandIndex_BIT_6,
        commandIndex_BIT_7,
        commandIndex_BIT_8,
        commandIndex_BIT_9,
        commandIndex_BIT_10,
        commandIndex_BIT_11,
        commandIndex_BIT_12,
        commandIndex_BIT_13,
        commandIndex_BIT_14,
        commandIndex_BIT_15,
        /**
        * SET (1): indicates that the command may write to NV CLEAR (0): indicates that the command does not write to NV
        */
        nv,
        /**
        * SET (1): This command could flush any number of loaded contexts. CLEAR (0): no additional changes other than indicated by the flushed attribute
        */
        extensive,
        /**
        * SET (1): The context associated with any transient handle in the command will be flushed when this command completes. CLEAR (0): No context is flushed as a side effect of this command.
        */
        flushed,
        /**
        * indicates the number of the handles in the handle area for this command
        */
        cHandles_BIT_0,
        cHandles_BIT_1,
        cHandles_BIT_2,
        /**
        * SET (1): indicates the presence of the handle area in the response
        */
        rHandle,
        /**
        * SET (1): indicates that the command is vendor-specific CLEAR (0): indicates that the command is defined in a version of this specification
        */
        V,
        /**
        * allocated for software; shall be zero
        */
        Res_BIT_0,
        Res_BIT_1
    }
    
    private static ValueMap<TPMA_CC>	_ValueMap = new ValueMap<TPMA_CC>();
    
    public static final TPMA_CC
    
        commandIndex_BIT_0 = new TPMA_CC(0x1, _N.commandIndex_BIT_0),
        commandIndex_BIT_1 = new TPMA_CC(0x2, _N.commandIndex_BIT_1),
        commandIndex_BIT_2 = new TPMA_CC(0x4, _N.commandIndex_BIT_2),
        commandIndex_BIT_3 = new TPMA_CC(0x8, _N.commandIndex_BIT_3),
        commandIndex_BIT_4 = new TPMA_CC(0x10, _N.commandIndex_BIT_4),
        commandIndex_BIT_5 = new TPMA_CC(0x20, _N.commandIndex_BIT_5),
        commandIndex_BIT_6 = new TPMA_CC(0x40, _N.commandIndex_BIT_6),
        commandIndex_BIT_7 = new TPMA_CC(0x80, _N.commandIndex_BIT_7),
        commandIndex_BIT_8 = new TPMA_CC(0x100, _N.commandIndex_BIT_8),
        commandIndex_BIT_9 = new TPMA_CC(0x200, _N.commandIndex_BIT_9),
        commandIndex_BIT_10 = new TPMA_CC(0x400, _N.commandIndex_BIT_10),
        commandIndex_BIT_11 = new TPMA_CC(0x800, _N.commandIndex_BIT_11),
        commandIndex_BIT_12 = new TPMA_CC(0x1000, _N.commandIndex_BIT_12),
        commandIndex_BIT_13 = new TPMA_CC(0x2000, _N.commandIndex_BIT_13),
        commandIndex_BIT_14 = new TPMA_CC(0x4000, _N.commandIndex_BIT_14),
        commandIndex_BIT_15 = new TPMA_CC(0x8000, _N.commandIndex_BIT_15),
        nv = new TPMA_CC(0x400000, _N.nv),
        extensive = new TPMA_CC(0x800000, _N.extensive),
        flushed = new TPMA_CC(0x1000000, _N.flushed),
        cHandles_BIT_0 = new TPMA_CC(0x2000000, _N.cHandles_BIT_0, true),
        cHandles_BIT_1 = new TPMA_CC(0x4000000, _N.cHandles_BIT_1, true),
        cHandles_BIT_2 = new TPMA_CC(0x8000000, _N.cHandles_BIT_2, true),
        rHandle = new TPMA_CC(0x10000000, _N.rHandle),
        V = new TPMA_CC(0x20000000, _N.V),
        Res_BIT_0 = new TPMA_CC(0x40000000, _N.Res_BIT_0, true),
        Res_BIT_1 = new TPMA_CC(0x80000000, _N.Res_BIT_1, true);
    public TPMA_CC (int value) { super(value, _ValueMap); }
    
    public TPMA_CC (TPMA_CC...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_CC fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_CC.class); }
    
    public static TPMA_CC fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_CC.class); }
    
    public static TPMA_CC fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_CC.class); }
    
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

