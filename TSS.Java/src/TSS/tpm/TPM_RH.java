package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 28 lists the architecturally defined handles that cannot be changed. The handles include authorization handles, and special handles.
*/
public final class TPM_RH extends TpmEnum<TPM_RH>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_RH. qualifier.
    public enum _N {
        FIRST,
        
        /**
        * not used1
        */
        SRK,
        
        /**
        * handle references the Storage Primary Seed (SPS), the ownerAuth, and the ownerPolicy
        */
        OWNER,
        
        /**
        * not used1
        */
        REVOKE,
        
        /**
        * not used1
        */
        TRANSPORT,
        
        /**
        * not used1
        */
        OPERATOR,
        
        /**
        * not used1
        */
        ADMIN,
        
        /**
        * not used1
        */
        EK,
        
        /**
        * a handle associated with the null hierarchy, an EmptyAuth authValue, and an Empty Policy authPolicy.
        */
        NULL,
        
        /**
        * value reserved to the TPM to indicate a handle location that has not been initialized or assigned
        */
        UNASSIGNED,
        
        /**
        * authorization value used to indicate a password authorization session
        */
        RS_PW,
        
        /**
        * references the authorization associated with the dictionary attack lockout reset
        */
        LOCKOUT,
        
        /**
        * references the Endorsement Primary Seed (EPS), endorsementAuth, and endorsementPolicy
        */
        ENDORSEMENT,
        
        /**
        * references the Platform Primary Seed (PPS), platformAuth, and platformPolicy
        */
        PLATFORM,
        
        /**
        * for phEnableNV
        */
        PLATFORM_NV,
        
        /**
        * Start of a range of authorization values that are vendor-specific. A TPM may support any of the values in this range as are needed for vendor-specific purposes. Disabled if ehEnable is CLEAR. NOTE Any includes none.
        */
        AUTH_00,
        
        /**
        * End of the range of vendor-specific authorization values.
        */
        AUTH_FF,
        
        /**
        * the top of the reserved handle area This is set to allow TPM2_GetCapability() to know where to stop. It may vary as implementations add to the permanent handle area.
        */
        LAST
        
    }
    
    private static ValueMap<TPM_RH> _ValueMap = new ValueMap<TPM_RH>();
    
    public static final TPM_RH
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        FIRST = new TPM_RH(0x40000000, _N.FIRST),
        SRK = new TPM_RH(0x40000000, _N.SRK),
        OWNER = new TPM_RH(0x40000001, _N.OWNER),
        REVOKE = new TPM_RH(0x40000002, _N.REVOKE),
        TRANSPORT = new TPM_RH(0x40000003, _N.TRANSPORT),
        OPERATOR = new TPM_RH(0x40000004, _N.OPERATOR),
        ADMIN = new TPM_RH(0x40000005, _N.ADMIN),
        EK = new TPM_RH(0x40000006, _N.EK),
        NULL = new TPM_RH(0x40000007, _N.NULL),
        UNASSIGNED = new TPM_RH(0x40000008, _N.UNASSIGNED),
        RS_PW = new TPM_RH(0x40000009, _N.RS_PW),
        LOCKOUT = new TPM_RH(0x4000000A, _N.LOCKOUT),
        ENDORSEMENT = new TPM_RH(0x4000000B, _N.ENDORSEMENT),
        PLATFORM = new TPM_RH(0x4000000C, _N.PLATFORM),
        PLATFORM_NV = new TPM_RH(0x4000000D, _N.PLATFORM_NV),
        AUTH_00 = new TPM_RH(0x40000010, _N.AUTH_00),
        AUTH_FF = new TPM_RH(0x4000010F, _N.AUTH_FF),
        LAST = new TPM_RH(0x4000010F, _N.LAST);
    public TPM_RH (int value) { super(value, _ValueMap); }
    
    public static TPM_RH fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_RH.class); }
    
    public static TPM_RH fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_RH.class); }
    
    public static TPM_RH fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_RH.class); }
    
    public TPM_RH._N asEnum() { return (TPM_RH._N)NameAsEnum; }
    
    public static Collection<TPM_RH> values() { return _ValueMap.values(); }
    
    private TPM_RH (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_RH (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

