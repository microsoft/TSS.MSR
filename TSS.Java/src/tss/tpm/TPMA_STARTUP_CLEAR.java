package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure may be read using TPM2_GetCapability(capability = TPM_CAP_TPM_PROPERTIES, property = TPM_PT_STARTUP_CLEAR).
*/
public final class TPMA_STARTUP_CLEAR extends TpmAttribute<TPMA_STARTUP_CLEAR>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_STARTUP_CLEAR. qualifier.
    public enum _N {
        /**
        * SET (1): The platform hierarchy is enabled and platformAuth or platformPolicy may be used for authorization. CLEAR (0): platformAuth and platformPolicy may not be used for authorizations, and objects in the platform hierarchy, including persistent objects, cannot be used. NOTE See TPM2_HierarchyControl in TPM 2.0 Part 3 for details on changing this attribute.
        */
        phEnable,
        /**
        * SET (1): The Storage hierarchy is enabled and ownerAuth or ownerPolicy may be used for authorization. NV indices defined using owner authorization are accessible. CLEAR (0): ownerAuth and ownerPolicy may not be used for authorizations, and objects in the Storage hierarchy, persistent objects, and NV indices defined using owner authorization cannot be used. NOTE See TPM2_HierarchyControl in TPM 2.0 Part 3 for details on changing this attribute.
        */
        shEnable,
        /**
        * SET (1): The EPS hierarchy is enabled and Endorsement Authorization may be used to authorize commands. CLEAR (0): Endorsement Authorization may not be used for authorizations, and objects in the endorsement hierarchy, including persistent objects, cannot be used. NOTE See TPM2_HierarchyControl in TPM 2.0 Part 3 for details on changing this attribute.
        */
        ehEnable,
        /**
        * SET (1): NV indices that have TPMA_NV_PLATFORMCREATE SET may be read or written. The platform can create define and undefine indices. CLEAR (0): NV indices that have TPMA_NV_PLATFORMCREATE SET may not be read or written (TPM_RC_HANDLE). The platform cannot define (TPM_RC_HIERARCHY) or undefined (TPM_RC_HANDLE) indices. NOTE See TPM2_HierarchyControl in TPM 2.0 Part 3 for details on changing this attribute. NOTE read refers to these commands: TPM2_NV_Read, TPM2_NV_ReadPublic, TPM_NV_Certify, TPM2_PolicyNV write refers to these commands: TPM2_NV_Write, TPM2_NV_Increment, TPM2_NV_Extend, TPM2_NV_SetBits NOTE The TPM must query the index TPMA_NV_PLATFORMCREATE attribute to determine whether phEnableNV is applicable. Since the TPM will return TPM_RC_HANDLE if the index does not exist, it also returns this error code if the index is disabled. Otherwise, the TPM would leak the existence of an index even when disabled.
        */
        phEnableNV,
        /**
        * SET (1): The TPM received a TPM2_Shutdown() and a matching TPM2_Startup(). CLEAR (0): TPM2_Startup(TPM_SU_CLEAR) was not preceded by a TPM2_Shutdown() of any type. NOTE A shutdown is orderly if the TPM receives a TPM2_Shutdown() of any type followed by a TPM2_Startup() of any type. However, the TPM will return an error if TPM2_Startup(TPM_SU_STATE) was not preceded by TPM2_Shutdown(TPM_SU_STATE).
        */
        orderly
    }
    
    private static ValueMap<TPMA_STARTUP_CLEAR>	_ValueMap = new ValueMap<TPMA_STARTUP_CLEAR>();
    
    public static final TPMA_STARTUP_CLEAR
    
        phEnable = new TPMA_STARTUP_CLEAR(0x1, _N.phEnable),
        shEnable = new TPMA_STARTUP_CLEAR(0x2, _N.shEnable),
        ehEnable = new TPMA_STARTUP_CLEAR(0x4, _N.ehEnable),
        phEnableNV = new TPMA_STARTUP_CLEAR(0x8, _N.phEnableNV),
        orderly = new TPMA_STARTUP_CLEAR(0x80000000, _N.orderly);
    public TPMA_STARTUP_CLEAR (int value) { super(value, _ValueMap); }
    
    public TPMA_STARTUP_CLEAR (TPMA_STARTUP_CLEAR...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_STARTUP_CLEAR fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_STARTUP_CLEAR.class); }
    
    public static TPMA_STARTUP_CLEAR fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_STARTUP_CLEAR.class); }
    
    public static TPMA_STARTUP_CLEAR fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_STARTUP_CLEAR.class); }
    
    public TPMA_STARTUP_CLEAR._N asEnum() { return (TPMA_STARTUP_CLEAR._N)NameAsEnum; }
    
    public static Collection<TPMA_STARTUP_CLEAR> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_STARTUP_CLEAR attr) { return super.hasAttr(attr); }
    
    public TPMA_STARTUP_CLEAR maskAttr (TPMA_STARTUP_CLEAR attr) { return super.maskAttr(attr, _ValueMap, TPMA_STARTUP_CLEAR.class); }
    
    private TPMA_STARTUP_CLEAR (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_STARTUP_CLEAR (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

