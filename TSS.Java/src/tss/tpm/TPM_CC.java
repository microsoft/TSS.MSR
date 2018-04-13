package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 12 lists the command codes and their attributes. The only normative column in this table is the column indicating the command code assigned to a specific command (the "Command Code" column). For all other columns, the command and response tables in TPM 2.0 Part 3 are definitive.
*/
public final class TPM_CC extends TpmEnum<TPM_CC>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_CC. qualifier.
    public enum _N {
        /**
        * Compile variable. May decrease based on implementation.
        */
        FIRST,
        
        NV_UndefineSpaceSpecial,
        
        EvictControl,
        
        HierarchyControl,
        
        NV_UndefineSpace,
        
        ChangeEPS,
        
        ChangePPS,
        
        Clear,
        
        ClearControl,
        
        ClockSet,
        
        HierarchyChangeAuth,
        
        NV_DefineSpace,
        
        PCR_Allocate,
        
        PCR_SetAuthPolicy,
        
        PP_Commands,
        
        SetPrimaryPolicy,
        
        FieldUpgradeStart,
        
        ClockRateAdjust,
        
        CreatePrimary,
        
        NV_GlobalWriteLock,
        
        GetCommandAuditDigest,
        
        NV_Increment,
        
        NV_SetBits,
        
        NV_Extend,
        
        NV_Write,
        
        NV_WriteLock,
        
        DictionaryAttackLockReset,
        
        DictionaryAttackParameters,
        
        NV_ChangeAuth,
        
        /**
        * PCR
        */
        PCR_Event,
        
        /**
        * PCR
        */
        PCR_Reset,
        
        SequenceComplete,
        
        SetAlgorithmSet,
        
        SetCommandCodeAuditStatus,
        
        FieldUpgradeData,
        
        IncrementalSelfTest,
        
        SelfTest,
        
        Startup,
        
        Shutdown,
        
        StirRandom,
        
        ActivateCredential,
        
        Certify,
        
        /**
        * Policy
        */
        PolicyNV,
        
        CertifyCreation,
        
        Duplicate,
        
        GetTime,
        
        GetSessionAuditDigest,
        
        NV_Read,
        
        NV_ReadLock,
        
        ObjectChangeAuth,
        
        /**
        * Policy
        */
        PolicySecret,
        
        Rewrap,
        
        Create,
        
        ECDH_ZGen,
        
        /**
        * See NOTE 1
        */
        HMAC,
        
        /**
        * See NOTE 1
        */
        MAC,
        
        Import,
        
        Load,
        
        Quote,
        
        RSA_Decrypt,
        
        /**
        * See NOTE 1
        */
        HMAC_Start,
        
        /**
        * See NOTE 1
        */
        MAC_Start,
        
        SequenceUpdate,
        
        Sign,
        
        Unseal,
        
        /**
        * Policy
        */
        PolicySigned,
        
        /**
        * Context
        */
        ContextLoad,
        
        /**
        * Context
        */
        ContextSave,
        
        ECDH_KeyGen,
        
        EncryptDecrypt,
        
        /**
        * Context
        */
        FlushContext,
        
        LoadExternal,
        
        MakeCredential,
        
        /**
        * NV
        */
        NV_ReadPublic,
        
        /**
        * Policy
        */
        PolicyAuthorize,
        
        /**
        * Policy
        */
        PolicyAuthValue,
        
        /**
        * Policy
        */
        PolicyCommandCode,
        
        /**
        * Policy
        */
        PolicyCounterTimer,
        
        /**
        * Policy
        */
        PolicyCpHash,
        
        /**
        * Policy
        */
        PolicyLocality,
        
        /**
        * Policy
        */
        PolicyNameHash,
        
        /**
        * Policy
        */
        PolicyOR,
        
        /**
        * Policy
        */
        PolicyTicket,
        
        ReadPublic,
        
        RSA_Encrypt,
        
        StartAuthSession,
        
        VerifySignature,
        
        ECC_Parameters,
        
        FirmwareRead,
        
        GetCapability,
        
        GetRandom,
        
        GetTestResult,
        
        Hash,
        
        /**
        * PCR
        */
        PCR_Read,
        
        /**
        * Policy
        */
        PolicyPCR,
        
        PolicyRestart,
        
        ReadClock,
        
        PCR_Extend,
        
        PCR_SetAuthValue,
        
        NV_Certify,
        
        EventSequenceComplete,
        
        HashSequenceStart,
        
        /**
        * Policy
        */
        PolicyPhysicalPresence,
        
        /**
        * Policy
        */
        PolicyDuplicationSelect,
        
        /**
        * Policy
        */
        PolicyGetDigest,
        
        TestParms,
        
        Commit,
        
        /**
        * Policy
        */
        PolicyPassword,
        
        ZGen_2Phase,
        
        EC_Ephemeral,
        
        /**
        * Policy
        */
        PolicyNvWritten,
        
        /**
        * Policy
        */
        PolicyTemplate,
        
        CreateLoaded,
        
        /**
        * Policy
        */
        PolicyAuthorizeNV,
        
        EncryptDecrypt2,
        
        AC_GetCapability,
        
        AC_Send,
        
        /**
        * Policy
        */
        Policy_AC_SendSelect,
        
        /**
        * Compile variable. May increase based on implementation.
        */
        LAST,
        
        CC_VEND,
        
        /**
        * Used for testing of command dispatch
        */
        Vendor_TCG_Test
        
    }
    
    private static ValueMap<TPM_CC> _ValueMap = new ValueMap<TPM_CC>();
    
    public static final TPM_CC
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        FIRST = new TPM_CC(0x0000011F, _N.FIRST),
        NV_UndefineSpaceSpecial = new TPM_CC(0x0000011F, _N.NV_UndefineSpaceSpecial),
        EvictControl = new TPM_CC(0x00000120, _N.EvictControl),
        HierarchyControl = new TPM_CC(0x00000121, _N.HierarchyControl),
        NV_UndefineSpace = new TPM_CC(0x00000122, _N.NV_UndefineSpace),
        ChangeEPS = new TPM_CC(0x00000124, _N.ChangeEPS),
        ChangePPS = new TPM_CC(0x00000125, _N.ChangePPS),
        Clear = new TPM_CC(0x00000126, _N.Clear),
        ClearControl = new TPM_CC(0x00000127, _N.ClearControl),
        ClockSet = new TPM_CC(0x00000128, _N.ClockSet),
        HierarchyChangeAuth = new TPM_CC(0x00000129, _N.HierarchyChangeAuth),
        NV_DefineSpace = new TPM_CC(0x0000012A, _N.NV_DefineSpace),
        PCR_Allocate = new TPM_CC(0x0000012B, _N.PCR_Allocate),
        PCR_SetAuthPolicy = new TPM_CC(0x0000012C, _N.PCR_SetAuthPolicy),
        PP_Commands = new TPM_CC(0x0000012D, _N.PP_Commands),
        SetPrimaryPolicy = new TPM_CC(0x0000012E, _N.SetPrimaryPolicy),
        FieldUpgradeStart = new TPM_CC(0x0000012F, _N.FieldUpgradeStart),
        ClockRateAdjust = new TPM_CC(0x00000130, _N.ClockRateAdjust),
        CreatePrimary = new TPM_CC(0x00000131, _N.CreatePrimary),
        NV_GlobalWriteLock = new TPM_CC(0x00000132, _N.NV_GlobalWriteLock),
        GetCommandAuditDigest = new TPM_CC(0x00000133, _N.GetCommandAuditDigest),
        NV_Increment = new TPM_CC(0x00000134, _N.NV_Increment),
        NV_SetBits = new TPM_CC(0x00000135, _N.NV_SetBits),
        NV_Extend = new TPM_CC(0x00000136, _N.NV_Extend),
        NV_Write = new TPM_CC(0x00000137, _N.NV_Write),
        NV_WriteLock = new TPM_CC(0x00000138, _N.NV_WriteLock),
        DictionaryAttackLockReset = new TPM_CC(0x00000139, _N.DictionaryAttackLockReset),
        DictionaryAttackParameters = new TPM_CC(0x0000013A, _N.DictionaryAttackParameters),
        NV_ChangeAuth = new TPM_CC(0x0000013B, _N.NV_ChangeAuth),
        PCR_Event = new TPM_CC(0x0000013C, _N.PCR_Event),
        PCR_Reset = new TPM_CC(0x0000013D, _N.PCR_Reset),
        SequenceComplete = new TPM_CC(0x0000013E, _N.SequenceComplete),
        SetAlgorithmSet = new TPM_CC(0x0000013F, _N.SetAlgorithmSet),
        SetCommandCodeAuditStatus = new TPM_CC(0x00000140, _N.SetCommandCodeAuditStatus),
        FieldUpgradeData = new TPM_CC(0x00000141, _N.FieldUpgradeData),
        IncrementalSelfTest = new TPM_CC(0x00000142, _N.IncrementalSelfTest),
        SelfTest = new TPM_CC(0x00000143, _N.SelfTest),
        Startup = new TPM_CC(0x00000144, _N.Startup),
        Shutdown = new TPM_CC(0x00000145, _N.Shutdown),
        StirRandom = new TPM_CC(0x00000146, _N.StirRandom),
        ActivateCredential = new TPM_CC(0x00000147, _N.ActivateCredential),
        Certify = new TPM_CC(0x00000148, _N.Certify),
        PolicyNV = new TPM_CC(0x00000149, _N.PolicyNV),
        CertifyCreation = new TPM_CC(0x0000014A, _N.CertifyCreation),
        Duplicate = new TPM_CC(0x0000014B, _N.Duplicate),
        GetTime = new TPM_CC(0x0000014C, _N.GetTime),
        GetSessionAuditDigest = new TPM_CC(0x0000014D, _N.GetSessionAuditDigest),
        NV_Read = new TPM_CC(0x0000014E, _N.NV_Read),
        NV_ReadLock = new TPM_CC(0x0000014F, _N.NV_ReadLock),
        ObjectChangeAuth = new TPM_CC(0x00000150, _N.ObjectChangeAuth),
        PolicySecret = new TPM_CC(0x00000151, _N.PolicySecret),
        Rewrap = new TPM_CC(0x00000152, _N.Rewrap),
        Create = new TPM_CC(0x00000153, _N.Create),
        ECDH_ZGen = new TPM_CC(0x00000154, _N.ECDH_ZGen),
        HMAC = new TPM_CC(0x00000155, _N.HMAC),
        MAC = new TPM_CC(0x00000155, _N.MAC),
        Import = new TPM_CC(0x00000156, _N.Import),
        Load = new TPM_CC(0x00000157, _N.Load),
        Quote = new TPM_CC(0x00000158, _N.Quote),
        RSA_Decrypt = new TPM_CC(0x00000159, _N.RSA_Decrypt),
        HMAC_Start = new TPM_CC(0x0000015B, _N.HMAC_Start),
        MAC_Start = new TPM_CC(0x0000015B, _N.MAC_Start),
        SequenceUpdate = new TPM_CC(0x0000015C, _N.SequenceUpdate),
        Sign = new TPM_CC(0x0000015D, _N.Sign),
        Unseal = new TPM_CC(0x0000015E, _N.Unseal),
        PolicySigned = new TPM_CC(0x00000160, _N.PolicySigned),
        ContextLoad = new TPM_CC(0x00000161, _N.ContextLoad),
        ContextSave = new TPM_CC(0x00000162, _N.ContextSave),
        ECDH_KeyGen = new TPM_CC(0x00000163, _N.ECDH_KeyGen),
        EncryptDecrypt = new TPM_CC(0x00000164, _N.EncryptDecrypt),
        FlushContext = new TPM_CC(0x00000165, _N.FlushContext),
        LoadExternal = new TPM_CC(0x00000167, _N.LoadExternal),
        MakeCredential = new TPM_CC(0x00000168, _N.MakeCredential),
        NV_ReadPublic = new TPM_CC(0x00000169, _N.NV_ReadPublic),
        PolicyAuthorize = new TPM_CC(0x0000016A, _N.PolicyAuthorize),
        PolicyAuthValue = new TPM_CC(0x0000016B, _N.PolicyAuthValue),
        PolicyCommandCode = new TPM_CC(0x0000016C, _N.PolicyCommandCode),
        PolicyCounterTimer = new TPM_CC(0x0000016D, _N.PolicyCounterTimer),
        PolicyCpHash = new TPM_CC(0x0000016E, _N.PolicyCpHash),
        PolicyLocality = new TPM_CC(0x0000016F, _N.PolicyLocality),
        PolicyNameHash = new TPM_CC(0x00000170, _N.PolicyNameHash),
        PolicyOR = new TPM_CC(0x00000171, _N.PolicyOR),
        PolicyTicket = new TPM_CC(0x00000172, _N.PolicyTicket),
        ReadPublic = new TPM_CC(0x00000173, _N.ReadPublic),
        RSA_Encrypt = new TPM_CC(0x00000174, _N.RSA_Encrypt),
        StartAuthSession = new TPM_CC(0x00000176, _N.StartAuthSession),
        VerifySignature = new TPM_CC(0x00000177, _N.VerifySignature),
        ECC_Parameters = new TPM_CC(0x00000178, _N.ECC_Parameters),
        FirmwareRead = new TPM_CC(0x00000179, _N.FirmwareRead),
        GetCapability = new TPM_CC(0x0000017A, _N.GetCapability),
        GetRandom = new TPM_CC(0x0000017B, _N.GetRandom),
        GetTestResult = new TPM_CC(0x0000017C, _N.GetTestResult),
        Hash = new TPM_CC(0x0000017D, _N.Hash),
        PCR_Read = new TPM_CC(0x0000017E, _N.PCR_Read),
        PolicyPCR = new TPM_CC(0x0000017F, _N.PolicyPCR),
        PolicyRestart = new TPM_CC(0x00000180, _N.PolicyRestart),
        ReadClock = new TPM_CC(0x00000181, _N.ReadClock),
        PCR_Extend = new TPM_CC(0x00000182, _N.PCR_Extend),
        PCR_SetAuthValue = new TPM_CC(0x00000183, _N.PCR_SetAuthValue),
        NV_Certify = new TPM_CC(0x00000184, _N.NV_Certify),
        EventSequenceComplete = new TPM_CC(0x00000185, _N.EventSequenceComplete),
        HashSequenceStart = new TPM_CC(0x00000186, _N.HashSequenceStart),
        PolicyPhysicalPresence = new TPM_CC(0x00000187, _N.PolicyPhysicalPresence),
        PolicyDuplicationSelect = new TPM_CC(0x00000188, _N.PolicyDuplicationSelect),
        PolicyGetDigest = new TPM_CC(0x00000189, _N.PolicyGetDigest),
        TestParms = new TPM_CC(0x0000018A, _N.TestParms),
        Commit = new TPM_CC(0x0000018B, _N.Commit),
        PolicyPassword = new TPM_CC(0x0000018C, _N.PolicyPassword),
        ZGen_2Phase = new TPM_CC(0x0000018D, _N.ZGen_2Phase),
        EC_Ephemeral = new TPM_CC(0x0000018E, _N.EC_Ephemeral),
        PolicyNvWritten = new TPM_CC(0x0000018F, _N.PolicyNvWritten),
        PolicyTemplate = new TPM_CC(0x00000190, _N.PolicyTemplate),
        CreateLoaded = new TPM_CC(0x00000191, _N.CreateLoaded),
        PolicyAuthorizeNV = new TPM_CC(0x00000192, _N.PolicyAuthorizeNV),
        EncryptDecrypt2 = new TPM_CC(0x00000193, _N.EncryptDecrypt2),
        AC_GetCapability = new TPM_CC(0x00000194, _N.AC_GetCapability),
        AC_Send = new TPM_CC(0x00000195, _N.AC_Send),
        Policy_AC_SendSelect = new TPM_CC(0x00000196, _N.Policy_AC_SendSelect),
        LAST = new TPM_CC(0x00000196, _N.LAST),
        CC_VEND = new TPM_CC(0x20000000, _N.CC_VEND),
        Vendor_TCG_Test = new TPM_CC(TPM_CC.CC_VEND.toInt()+0x0000, _N.Vendor_TCG_Test);
    public TPM_CC (int value) { super(value, _ValueMap); }
    
    public static TPM_CC fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_CC.class); }
    
    public static TPM_CC fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_CC.class); }
    
    public static TPM_CC fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_CC.class); }
    
    public TPM_CC._N asEnum() { return (TPM_CC._N)NameAsEnum; }
    
    public static Collection<TPM_CC> values() { return _ValueMap.values(); }
    
    private TPM_CC (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_CC (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

