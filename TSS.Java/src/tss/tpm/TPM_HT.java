package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* The 32-bit handle space is divided into 256 regions of equal size with 224 values in each. Each of these ranges represents a handle type.
*/
public final class TPM_HT extends TpmEnum<TPM_HT>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_HT. qualifier.
    public enum _N {
        /**
        * PCR consecutive numbers, starting at 0, that reference the PCR registers A platform-specific specification will set the minimum number of PCR and an implementation may have more.
        */
        PCR,
        
        /**
        * NV Index assigned by the caller
        */
        NV_INDEX,
        
        /**
        * HMAC Authorization Session assigned by the TPM when the session is created
        */
        HMAC_SESSION,
        
        /**
        * Loaded Authorization Session used only in the context of TPM2_GetCapability This type references both loaded HMAC and loaded policy authorization sessions.
        */
        LOADED_SESSION,
        
        /**
        * Policy Authorization Session assigned by the TPM when the session is created
        */
        POLICY_SESSION,
        
        /**
        * Saved Authorization Session used only in the context of TPM2_GetCapability This type references saved authorization session contexts for which the TPM is maintaining tracking information.
        */
        SAVED_SESSION,
        
        /**
        * Permanent Values assigned by this specification in Table 28
        */
        PERMANENT,
        
        /**
        * Transient Objects assigned by the TPM when an object is loaded into transient-object memory or when a persistent object is converted to a transient object
        */
        TRANSIENT,
        
        /**
        * Persistent Objects assigned by the TPM when a loaded transient object is made persistent
        */
        PERSISTENT,
        
        /**
        * Attached Component handle for an Attached Component.
        */
        AC
        
    }
    
    private static ValueMap<TPM_HT> _ValueMap = new ValueMap<TPM_HT>();
    
    public static final TPM_HT
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        PCR = new TPM_HT(0x00, _N.PCR),
        NV_INDEX = new TPM_HT(0x01, _N.NV_INDEX),
        HMAC_SESSION = new TPM_HT(0x02, _N.HMAC_SESSION),
        LOADED_SESSION = new TPM_HT(0x02, _N.LOADED_SESSION, true),
        POLICY_SESSION = new TPM_HT(0x03, _N.POLICY_SESSION),
        SAVED_SESSION = new TPM_HT(0x03, _N.SAVED_SESSION, true),
        PERMANENT = new TPM_HT(0x40, _N.PERMANENT),
        TRANSIENT = new TPM_HT(0x80, _N.TRANSIENT),
        PERSISTENT = new TPM_HT(0x81, _N.PERSISTENT),
        AC = new TPM_HT(0x90, _N.AC);
    public TPM_HT (int value) { super(value, _ValueMap); }
    
    public static TPM_HT fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_HT.class); }
    
    public static TPM_HT fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_HT.class); }
    
    public static TPM_HT fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_HT.class); }
    
    public TPM_HT._N asEnum() { return (TPM_HT._N)NameAsEnum; }
    
    public static Collection<TPM_HT> values() { return _ValueMap.values(); }
    
    private TPM_HT (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_HT (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 1; }
}

//<<<

