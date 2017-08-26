package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure of this attribute is used to report the memory management method used by the TPM for transient objects and authorization sessions. This structure may be read using TPM2_GetCapability(capability = TPM_CAP_TPM_PROPERTIES, property = TPM_PT_MEMORY).
*/
public final class TPMA_MEMORY extends TpmAttribute<TPMA_MEMORY>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_MEMORY. qualifier.
    public enum _N {
        /**
        * SET (1): indicates that the RAM memory used for authorization session contexts is shared with the memory used for transient objects CLEAR (0): indicates that the memory used for authorization sessions is not shared with memory used for transient objects
        */
        sharedRAM,
        /**
        * SET (1): indicates that the NV memory used for persistent objects is shared with the NV memory used for NV Index values CLEAR (0): indicates that the persistent objects and NV Index values are allocated from separate sections of NV
        */
        sharedNV,
        /**
        * SET (1): indicates that the TPM copies persistent objects to a transient-object slot in RAM when the persistent object is referenced in a command. The TRM is required to make sure that an object slot is available. CLEAR (0): indicates that the TPM does not use transient-object slots when persistent objects are referenced
        */
        objectCopiedToRam
    }
    
    private static ValueMap<TPMA_MEMORY>	_ValueMap = new ValueMap<TPMA_MEMORY>();
    
    public static final TPMA_MEMORY
    
        sharedRAM = new TPMA_MEMORY(0x1, _N.sharedRAM),
        sharedNV = new TPMA_MEMORY(0x2, _N.sharedNV),
        objectCopiedToRam = new TPMA_MEMORY(0x4, _N.objectCopiedToRam);
    public TPMA_MEMORY (int value) { super(value, _ValueMap); }
    
    public TPMA_MEMORY (TPMA_MEMORY...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_MEMORY fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_MEMORY.class); }
    
    public static TPMA_MEMORY fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_MEMORY.class); }
    
    public static TPMA_MEMORY fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_MEMORY.class); }
    
    public TPMA_MEMORY._N asEnum() { return (TPMA_MEMORY._N)NameAsEnum; }
    
    public static Collection<TPMA_MEMORY> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_MEMORY attr) { return super.hasAttr(attr); }
    
    public TPMA_MEMORY maskAttr (TPMA_MEMORY attr) { return super.maskAttr(attr, _ValueMap, TPMA_MEMORY.class); }
    
    private TPMA_MEMORY (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_MEMORY (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

