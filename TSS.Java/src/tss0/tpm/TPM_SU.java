package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* These values are used in TPM2_Startup() to indicate the shutdown and startup mode. The defined startup sequences are:
*/
public final class TPM_SU extends TpmEnum<TPM_SU>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_SU. qualifier.
    public enum _N {
        /**
        * on TPM2_Shutdown(), indicates that the TPM should prepare for loss of power and save state required for an orderly startup (TPM Reset). on TPM2_Startup(), indicates that the TPM should perform TPM Reset or TPM Restart
        */
        CLEAR,
        
        /**
        * on TPM2_Shutdown(), indicates that the TPM should prepare for loss of power and save state required for an orderly startup (TPM Restart or TPM Resume) on TPM2_Startup(), indicates that the TPM should restore the state saved by TPM2_Shutdown(TPM_SU_STATE)
        */
        STATE
        
    }
    
    private static ValueMap<TPM_SU> _ValueMap = new ValueMap<TPM_SU>();
    
    public static final TPM_SU
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        CLEAR = new TPM_SU(0x0000, _N.CLEAR),
        STATE = new TPM_SU(0x0001, _N.STATE);
    public TPM_SU (int value) { super(value, _ValueMap); }
    
    public static TPM_SU fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_SU.class); }
    
    public static TPM_SU fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_SU.class); }
    
    public static TPM_SU fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_SU.class); }
    
    public TPM_SU._N asEnum() { return (TPM_SU._N)NameAsEnum; }
    
    public static Collection<TPM_SU> values() { return _ValueMap.values(); }
    
    private TPM_SU (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_SU (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 2; }
}

//<<<

