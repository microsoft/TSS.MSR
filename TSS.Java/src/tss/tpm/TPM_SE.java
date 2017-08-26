package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This type is used in TPM2_StartAuthSession() to indicate the type of the session to be created.
*/
public final class TPM_SE extends TpmEnum<TPM_SE>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_SE. qualifier.
    public enum _N {
        HMAC,
        
        POLICY,
        
        /**
        * The policy session is being used to compute the policyHash and not for command authorization. This setting modifies some policy commands and prevents session from being used to authorize a command.
        */
        TRIAL
        
    }
    
    private static ValueMap<TPM_SE> _ValueMap = new ValueMap<TPM_SE>();
    
    public static final TPM_SE
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        HMAC = new TPM_SE(0x00, _N.HMAC),
        POLICY = new TPM_SE(0x01, _N.POLICY),
        TRIAL = new TPM_SE(0x03, _N.TRIAL);
    public TPM_SE (int value) { super(value, _ValueMap); }
    
    public static TPM_SE fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_SE.class); }
    
    public static TPM_SE fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_SE.class); }
    
    public static TPM_SE fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_SE.class); }
    
    public TPM_SE._N asEnum() { return (TPM_SE._N)NameAsEnum; }
    
    public static Collection<TPM_SE> values() { return _ValueMap.values(); }
    
    private TPM_SE (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_SE (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 1; }
}

//<<<

