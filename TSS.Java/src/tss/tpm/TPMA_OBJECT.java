package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This attribute structure indicates an objects use, its authorization types, and its relationship to other objects.
*/
public final class TPMA_OBJECT extends TpmAttribute<TPMA_OBJECT>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_OBJECT. qualifier.
    public enum _N {
        /**
        * SET (1): The hierarchy of the object, as indicated by its Qualified Name, may not change. CLEAR (0): The hierarchy of the object may change as a result of this object or an ancestor key being duplicated for use in another hierarchy. NOTE fixedTPM does not indicate that key material resides on a single TPM. (see sensitiveDataOrigin).
        */
        fixedTPM,
        /**
        * SET (1): Previously saved contexts of this object may not be loaded after Startup(CLEAR). CLEAR (0): Saved contexts of this object may be used after a Shutdown(STATE) and subsequent Startup().
        */
        stClear,
        /**
        * SET (1): The parent of the object may not change. CLEAR (0): The parent of the object may change as the result of a TPM2_Duplicate() of the object.
        */
        fixedParent,
        /**
        * SET (1): Indicates that, when the object was created with TPM2_Create() or TPM2_CreatePrimary(), the TPM generated all of the sensitive data other than the authValue. CLEAR (0): A portion of the sensitive data, other than the authValue, was provided by the caller.
        */
        sensitiveDataOrigin,
        /**
        * SET (1): Approval of USER role actions with this object may be with an HMAC session or with a password using the authValue of the object or a policy session. CLEAR (0): Approval of USER role actions with this object may only be done with a policy session.
        */
        userWithAuth,
        /**
        * SET (1): Approval of ADMIN role actions with this object may only be done with a policy session. CLEAR (0): Approval of ADMIN role actions with this object may be with an HMAC session or with a password using the authValue of the object or a policy session.
        */
        adminWithPolicy,
        /**
        * SET (1): The object is not subject to dictionary attack protections. CLEAR (0): The object is subject to dictionary attack protections.
        */
        noDA,
        /**
        * SET (1): If the object is duplicated, then symmetricAlg shall not be TPM_ALG_NULL and newParentHandle shall not be TPM_RH_NULL. CLEAR (0): The object may be duplicated without an inner wrapper on the private portion of the object and the new parent may be TPM_RH_NULL.
        */
        encryptedDuplication,
        /**
        * SET (1): Key usage is restricted to manipulate structures of known format; the parent of this key shall have restricted SET. CLEAR (0): Key usage is not restricted to use on special formats.
        */
        restricted,
        /**
        * SET (1): The private portion of the key may be used to decrypt. CLEAR (0): The private portion of the key may not be used to decrypt.
        */
        decrypt,
        /**
        * SET (1): For a symmetric cipher object, the private portion of the key may be used to encrypt. For other objects, the private portion of the key may be used to sign. CLEAR (0): The private portion of the key may not be used to sign or encrypt.
        */
        sign,
        /**
        * Alias to the Sign value.
        */
        encrypt
    }
    
    private static ValueMap<TPMA_OBJECT>	_ValueMap = new ValueMap<TPMA_OBJECT>();
    
    public static final TPMA_OBJECT
    
        fixedTPM = new TPMA_OBJECT(0x2, _N.fixedTPM),
        stClear = new TPMA_OBJECT(0x4, _N.stClear),
        fixedParent = new TPMA_OBJECT(0x10, _N.fixedParent),
        sensitiveDataOrigin = new TPMA_OBJECT(0x20, _N.sensitiveDataOrigin),
        userWithAuth = new TPMA_OBJECT(0x40, _N.userWithAuth),
        adminWithPolicy = new TPMA_OBJECT(0x80, _N.adminWithPolicy),
        noDA = new TPMA_OBJECT(0x400, _N.noDA),
        encryptedDuplication = new TPMA_OBJECT(0x800, _N.encryptedDuplication),
        restricted = new TPMA_OBJECT(0x10000, _N.restricted),
        decrypt = new TPMA_OBJECT(0x20000, _N.decrypt),
        sign = new TPMA_OBJECT(0x40000, _N.sign),
        encrypt = new TPMA_OBJECT(0x40000, _N.encrypt);
    public TPMA_OBJECT (int value) { super(value, _ValueMap); }
    
    public TPMA_OBJECT (TPMA_OBJECT...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_OBJECT fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_OBJECT.class); }
    
    public static TPMA_OBJECT fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_OBJECT.class); }
    
    public static TPMA_OBJECT fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_OBJECT.class); }
    
    public TPMA_OBJECT._N asEnum() { return (TPMA_OBJECT._N)NameAsEnum; }
    
    public static Collection<TPMA_OBJECT> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_OBJECT attr) { return super.hasAttr(attr); }
    
    public TPMA_OBJECT maskAttr (TPMA_OBJECT attr) { return super.maskAttr(attr, _ValueMap, TPMA_OBJECT.class); }
    
    private TPMA_OBJECT (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_OBJECT (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

