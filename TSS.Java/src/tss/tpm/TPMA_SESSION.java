package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This octet in each session is used to identify the session type, indicate its relationship to any handles in the command, and indicate its use in parameter encryption.
*/
public final class TPMA_SESSION extends TpmAttribute<TPMA_SESSION>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_SESSION. qualifier.
    public enum _N {
        /**
        * SET (1): In a command, this setting indicates that the session is to remain active after successful completion of the command. In a response, it indicates that the session is still active. If SET in the command, this attribute shall be SET in the response. CLEAR (0): In a command, this setting indicates that the TPM should close the session and flush any related context when the command completes successfully. In a response, it indicates that the session is closed and the context is no longer active. This attribute has no meaning for a password authorization and the TPM will allow any setting of the attribute in the command and SET the attribute in the response. This attribute will only be CLEAR in one response for a logical session. If the attribute is CLEAR, the context associated with the session is no longer in use and the space is available. A session created after another session is ended may have the same handle but logically is not the same session. This attribute has no effect if the command does not complete successfully.
        */
        continueSession,
        /**
        * SET (1): In a command, this setting indicates that the command should only be executed if the session is exclusive at the start of the command. In a response, it indicates that the session is exclusive. This setting is only allowed if the audit attribute is SET (TPM_RC_ATTRIBUTES). CLEAR (0): In a command, indicates that the session need not be exclusive at the start of the command. In a response, indicates that the session is not exclusive.
        */
        auditExclusive,
        /**
        * SET (1): In a command, this setting indicates that the audit digest of the session should be initialized and the exclusive status of the session SET. This setting is only allowed if the audit attribute is SET (TPM_RC_ATTRIBUTES). CLEAR (0): In a command, indicates that the audit digest should not be initialized. This bit is always CLEAR in a response.
        */
        auditReset,
        /**
        * SET (1): In a command, this setting indicates that the first parameter in the command is symmetrically encrypted using the parameter encryption scheme described in TPM 2.0 Part 1. The TPM will decrypt the parameter after performing any HMAC computations and before unmarshaling the parameter. In a response, the attribute is copied from the request but has no effect on the response. CLEAR (0): Session not used for encryption. For a password authorization, this attribute will be CLEAR in both the command and response. This attribute may be SET in a session that is not associated with a command handle. Such a session is provided for purposes of encrypting a parameter and not for authorization. This attribute may be SET in combination with any other session attributes.
        */
        decrypt,
        /**
        * SET (1): In a command, this setting indicates that the TPM should use this session to encrypt the first parameter in the response. In a response, it indicates that the attribute was set in the command and that the TPM used the session to encrypt the first parameter in the response using the parameter encryption scheme described in TPM 2.0 Part 1. CLEAR (0): Session not used for encryption. For a password authorization, this attribute will be CLEAR in both the command and response. This attribute may be SET in a session that is not associated with a command handle. Such a session is provided for purposes of encrypting a parameter and not for authorization.
        */
        encrypt,
        /**
        * SET (1): In a command or response, this setting indicates that the session is for audit and that auditExclusive and auditReset have meaning. This session may also be used for authorization, encryption, or decryption. The encrypted and encrypt fields may be SET or CLEAR. CLEAR (0): Session is not used for audit. If SET in the command, then this attribute will be SET in the response.
        */
        audit
    }
    
    private static ValueMap<TPMA_SESSION>	_ValueMap = new ValueMap<TPMA_SESSION>();
    
    public static final TPMA_SESSION
    
        continueSession = new TPMA_SESSION(0x1, _N.continueSession),
        auditExclusive = new TPMA_SESSION(0x2, _N.auditExclusive),
        auditReset = new TPMA_SESSION(0x4, _N.auditReset),
        decrypt = new TPMA_SESSION(0x20, _N.decrypt),
        encrypt = new TPMA_SESSION(0x40, _N.encrypt),
        audit = new TPMA_SESSION(0x80, _N.audit);
    public TPMA_SESSION (int value) { super(value, _ValueMap); }
    
    public TPMA_SESSION (TPMA_SESSION...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_SESSION fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_SESSION.class); }
    
    public static TPMA_SESSION fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_SESSION.class); }
    
    public static TPMA_SESSION fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_SESSION.class); }
    
    public TPMA_SESSION._N asEnum() { return (TPMA_SESSION._N)NameAsEnum; }
    
    public static Collection<TPMA_SESSION> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_SESSION attr) { return super.hasAttr(attr); }
    
    public TPMA_SESSION maskAttr (TPMA_SESSION attr) { return super.maskAttr(attr, _ValueMap, TPMA_SESSION.class); }
    
    private TPMA_SESSION (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_SESSION (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 1; }
}

//<<<

