package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* The attributes in this structure are persistent and are not changed as a result of _TPM_Init or any TPM2_Startup(). Some of the attributes in this structure may change as the result of specific Protected Capabilities. This structure may be read using TPM2_GetCapability(capability = TPM_CAP_TPM_PROPERTIES, property = TPM_PT_PERMANENT).
*/
public final class TPMA_PERMANENT extends TpmAttribute<TPMA_PERMANENT>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_PERMANENT. qualifier.
    public enum _N {
        /**
        * SET (1): TPM2_HierarchyChangeAuth() with ownerAuth has been executed since the last TPM2_Clear(). CLEAR (0): ownerAuth has not been changed since TPM2_Clear().
        */
        ownerAuthSet,
        /**
        * SET (1): TPM2_HierarchyChangeAuth() with endorsementAuth has been executed since the last TPM2_Clear(). CLEAR (0): endorsementAuth has not been changed since TPM2_Clear().
        */
        endorsementAuthSet,
        /**
        * SET (1): TPM2_HierarchyChangeAuth() with lockoutAuth has been executed since the last TPM2_Clear(). CLEAR (0): lockoutAuth has not been changed since TPM2_Clear().
        */
        lockoutAuthSet,
        /**
        * SET (1): TPM2_Clear() is disabled. CLEAR (0): TPM2_Clear() is enabled. NOTE See TPM2_ClearControl in TPM 2.0 Part 3 for details on changing this attribute.
        */
        disableClear,
        /**
        * SET (1): The TPM is in lockout, when failedTries is equal to maxTries.
        */
        inLockout,
        /**
        * SET (1): The EPS was created by the TPM. CLEAR (0): The EPS was created outside of the TPM using a manufacturer-specific process.
        */
        tpmGeneratedEPS
    }
    
    private static ValueMap<TPMA_PERMANENT>	_ValueMap = new ValueMap<TPMA_PERMANENT>();
    
    public static final TPMA_PERMANENT
    
        ownerAuthSet = new TPMA_PERMANENT(0x1, _N.ownerAuthSet),
        endorsementAuthSet = new TPMA_PERMANENT(0x2, _N.endorsementAuthSet),
        lockoutAuthSet = new TPMA_PERMANENT(0x4, _N.lockoutAuthSet),
        disableClear = new TPMA_PERMANENT(0x100, _N.disableClear),
        inLockout = new TPMA_PERMANENT(0x200, _N.inLockout),
        tpmGeneratedEPS = new TPMA_PERMANENT(0x400, _N.tpmGeneratedEPS);
    public TPMA_PERMANENT (int value) { super(value, _ValueMap); }
    
    public TPMA_PERMANENT (TPMA_PERMANENT...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_PERMANENT fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_PERMANENT.class); }
    
    public static TPMA_PERMANENT fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_PERMANENT.class); }
    
    public static TPMA_PERMANENT fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_PERMANENT.class); }
    
    public TPMA_PERMANENT._N asEnum() { return (TPMA_PERMANENT._N)NameAsEnum; }
    
    public static Collection<TPMA_PERMANENT> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_PERMANENT attr) { return super.hasAttr(attr); }
    
    public TPMA_PERMANENT maskAttr (TPMA_PERMANENT attr) { return super.maskAttr(attr, _ValueMap, TPMA_PERMANENT.class); }
    
    private TPMA_PERMANENT (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_PERMANENT (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

