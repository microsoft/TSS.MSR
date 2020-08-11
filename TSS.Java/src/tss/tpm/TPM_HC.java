package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>

/** The definitions in Table 29 are used to define many of the interface data types.  */
public final class TPM_HC extends TpmEnum<TPM_HC>
{
    /** Values from enum _N are only intended to be used in case labels of a switch statement
     *  using the result of this.asEnum() method as the switch condition. However, their Java
     *  names are identical to those of the constants defined in this class further below, so
     *  for any other usage just prepend them with the TPM_HC. qualifier.
     */
    public enum _N {
        /** To mask off the HR  */
        HR_HANDLE_MASK, 
        
        /** To mask off the variable part  */
        HR_RANGE_MASK, 
        
        HR_SHIFT, 
        
        HR_PCR, 
        
        HR_HMAC_SESSION, 
        
        HR_POLICY_SESSION, 
        
        HR_TRANSIENT, 
        
        HR_PERSISTENT, 
        
        HR_NV_INDEX, 
        
        HR_PERMANENT, 
        
        /** First PCR  */
        PCR_FIRST, 
        
        /** Last PCR  */
        PCR_LAST, 
        
        /** First HMAC session  */
        HMAC_SESSION_FIRST, 
        
        /** Last HMAC session  */
        HMAC_SESSION_LAST, 
        
        /** Used in GetCapability  */
        LOADED_SESSION_FIRST, 
        
        /** Used in GetCapability  */
        LOADED_SESSION_LAST, 
        
        /** First policy session  */
        POLICY_SESSION_FIRST, 
        
        /** Last policy session  */
        POLICY_SESSION_LAST, 
        
        /** First transient object  */
        TRANSIENT_FIRST, 
        
        /** Used in GetCapability  */
        ACTIVE_SESSION_FIRST, 
        
        /** Used in GetCapability  */
        ACTIVE_SESSION_LAST, 
        
        /** Last transient object  */
        TRANSIENT_LAST, 
        
        /** First persistent object  */
        PERSISTENT_FIRST, 
        
        /** Last persistent object  */
        PERSISTENT_LAST, 
        
        /** First platform persistent object  */
        PLATFORM_PERSISTENT, 
        
        /** First allowed NV Index  */
        NV_INDEX_FIRST, 
        
        /** Last allowed NV Index  */
        NV_INDEX_LAST, 
        
        PERMANENT_FIRST, 
        
        PERMANENT_LAST, 
        
        /** AC aliased NV Index  */
        HR_NV_AC, 
        
        /** First NV Index aliased to Attached Component  */
        NV_AC_FIRST, 
        
        /** Last NV Index aliased to Attached Component  */
        NV_AC_LAST, 
        
        /** AC Handle  */
        HR_AC, 
        
        /** First Attached Component  */
        AC_FIRST, 
        
        /** Last Attached Component  */
        AC_LAST
    }
    
    private static ValueMap<TPM_HC> _ValueMap = new ValueMap<TPM_HC>();
    
    /** These definitions provide mapping of the Java enum constants to their TPM integer values  */
    public static final TPM_HC
        HR_HANDLE_MASK = new TPM_HC(0x00FFFFFF, _N.HR_HANDLE_MASK),
        HR_RANGE_MASK = new TPM_HC(0xFF000000, _N.HR_RANGE_MASK),
        HR_SHIFT = new TPM_HC(24, _N.HR_SHIFT),
        HR_PCR = new TPM_HC((TPM_HT.PCR.toInt() << HR_SHIFT.toInt()), _N.HR_PCR),
        HR_HMAC_SESSION = new TPM_HC((TPM_HT.HMAC_SESSION.toInt() << HR_SHIFT.toInt()), _N.HR_HMAC_SESSION),
        HR_POLICY_SESSION = new TPM_HC((TPM_HT.POLICY_SESSION.toInt() << HR_SHIFT.toInt()), _N.HR_POLICY_SESSION),
        HR_TRANSIENT = new TPM_HC((TPM_HT.TRANSIENT.toInt() << HR_SHIFT.toInt()), _N.HR_TRANSIENT),
        HR_PERSISTENT = new TPM_HC((TPM_HT.PERSISTENT.toInt() << HR_SHIFT.toInt()), _N.HR_PERSISTENT),
        HR_NV_INDEX = new TPM_HC((TPM_HT.NV_INDEX.toInt() << HR_SHIFT.toInt()), _N.HR_NV_INDEX),
        HR_PERMANENT = new TPM_HC((TPM_HT.PERMANENT.toInt() << HR_SHIFT.toInt()), _N.HR_PERMANENT),
        PCR_FIRST = new TPM_HC((HR_PCR.toInt() + 0), _N.PCR_FIRST),
        PCR_LAST = new TPM_HC((PCR_FIRST.toInt() + Implementation.IMPLEMENTATION_PCR.toInt()-1), _N.PCR_LAST),
        HMAC_SESSION_FIRST = new TPM_HC((HR_HMAC_SESSION.toInt() + 0), _N.HMAC_SESSION_FIRST),
        HMAC_SESSION_LAST = new TPM_HC((HMAC_SESSION_FIRST.toInt()+Implementation.MAX_ACTIVE_SESSIONS.toInt()-1), _N.HMAC_SESSION_LAST),
        LOADED_SESSION_FIRST = new TPM_HC(HMAC_SESSION_FIRST.toInt(), _N.LOADED_SESSION_FIRST),
        LOADED_SESSION_LAST = new TPM_HC(HMAC_SESSION_LAST.toInt(), _N.LOADED_SESSION_LAST),
        POLICY_SESSION_FIRST = new TPM_HC((HR_POLICY_SESSION.toInt() + 0), _N.POLICY_SESSION_FIRST),
        POLICY_SESSION_LAST = new TPM_HC((POLICY_SESSION_FIRST.toInt() + Implementation.MAX_ACTIVE_SESSIONS.toInt()-1), _N.POLICY_SESSION_LAST),
        TRANSIENT_FIRST = new TPM_HC((HR_TRANSIENT.toInt() + 0), _N.TRANSIENT_FIRST),
        ACTIVE_SESSION_FIRST = new TPM_HC(POLICY_SESSION_FIRST.toInt(), _N.ACTIVE_SESSION_FIRST),
        ACTIVE_SESSION_LAST = new TPM_HC(POLICY_SESSION_LAST.toInt(), _N.ACTIVE_SESSION_LAST),
        TRANSIENT_LAST = new TPM_HC((TRANSIENT_FIRST.toInt()+Implementation.MAX_LOADED_OBJECTS.toInt()-1), _N.TRANSIENT_LAST),
        PERSISTENT_FIRST = new TPM_HC((HR_PERSISTENT.toInt() + 0), _N.PERSISTENT_FIRST),
        PERSISTENT_LAST = new TPM_HC((PERSISTENT_FIRST.toInt() + 0x00FFFFFF), _N.PERSISTENT_LAST),
        PLATFORM_PERSISTENT = new TPM_HC((PERSISTENT_FIRST.toInt() + 0x00800000), _N.PLATFORM_PERSISTENT),
        NV_INDEX_FIRST = new TPM_HC((HR_NV_INDEX.toInt() + 0), _N.NV_INDEX_FIRST),
        NV_INDEX_LAST = new TPM_HC((NV_INDEX_FIRST.toInt() + 0x00FFFFFF), _N.NV_INDEX_LAST),
        PERMANENT_FIRST = new TPM_HC(TPM_RH.FIRST.toInt(), _N.PERMANENT_FIRST),
        PERMANENT_LAST = new TPM_HC(TPM_RH.LAST.toInt(), _N.PERMANENT_LAST),
        HR_NV_AC = new TPM_HC(((TPM_HT.NV_INDEX.toInt() << HR_SHIFT.toInt()) + 0xD00000), _N.HR_NV_AC),
        NV_AC_FIRST = new TPM_HC((HR_NV_AC.toInt() + 0), _N.NV_AC_FIRST),
        NV_AC_LAST = new TPM_HC((HR_NV_AC.toInt() + 0x0000FFFF), _N.NV_AC_LAST),
        HR_AC = new TPM_HC((TPM_HT.AC.toInt() << HR_SHIFT.toInt()), _N.HR_AC),
        AC_FIRST = new TPM_HC((HR_AC.toInt() + 0), _N.AC_FIRST),
        AC_LAST = new TPM_HC((HR_AC.toInt() + 0x0000FFFF), _N.AC_LAST);
    public TPM_HC () { super(0, _ValueMap); }
    public TPM_HC (int value) { super(value, _ValueMap); }
    public static TPM_HC fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_HC.class); }
    public static TPM_HC fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_HC.class); }
    public static TPM_HC fromTpm (TpmBuffer buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_HC.class); }
    public TPM_HC._N asEnum() { return (TPM_HC._N)NameAsEnum; }
    public static Collection<TPM_HC> values() { return _ValueMap.values(); }
    private TPM_HC (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    private TPM_HC (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }

    @Override
    protected int wireSize() { return 4; }
}

//<<<
