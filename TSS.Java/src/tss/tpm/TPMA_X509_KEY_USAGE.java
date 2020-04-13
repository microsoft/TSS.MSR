package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  These attributes are as specified in clause 4.2.1.3. of RFC 5280 Internet X.509 Public Key
 *  Infrastructure Certificate and Certificate Revocation List (CRL) Profile. For
 *  TPM2_CertifyX509, when a caller provides a DER encoded Key Usage in partialCertificate,
 *  the TPM will validate that the key to be certified meets the requirements of Key Usage.
 */
public final class TPMA_X509_KEY_USAGE extends TpmAttribute<TPMA_X509_KEY_USAGE>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_X509_KEY_USAGE. qualifier.
    public enum _N {
        /** Attributes.Decrypt SET */
        decipherOnly,
        
        /** Attributes.Decrypt SET */
        encipherOnly,
        
        /** Attributes.sign SET */
        cRLSign,
        
        /** Attributes.sign SET */
        keyCertSign,
        
        /** Attributes.Decrypt SET */
        keyAgreement,
        
        /** Attributes.Decrypt SET */
        dataEncipherment,
        
        /** asymmetric key with decrypt and restricted SET key has the attributes of a parent key */
        keyEncipherment,
        
        /** fixedTPM SET in Subject Key (objectHandle) */
        nonrepudiation,
        
        /** Alias to the nonrepudiation value. */
        contentCommitment,
        
        /** sign SET in Subject Key (objectHandle) */
        digitalSignature
    }

    private static ValueMap<TPMA_X509_KEY_USAGE>	_ValueMap = new ValueMap<TPMA_X509_KEY_USAGE>();
    
    /** These definitions provide mapping of the Java enum constants to their TPM integer values */
    public static final TPMA_X509_KEY_USAGE
        decipherOnly = new TPMA_X509_KEY_USAGE(0x800000, _N.decipherOnly),
        encipherOnly = new TPMA_X509_KEY_USAGE(0x1000000, _N.encipherOnly),
        cRLSign = new TPMA_X509_KEY_USAGE(0x2000000, _N.cRLSign),
        keyCertSign = new TPMA_X509_KEY_USAGE(0x4000000, _N.keyCertSign),
        keyAgreement = new TPMA_X509_KEY_USAGE(0x8000000, _N.keyAgreement),
        dataEncipherment = new TPMA_X509_KEY_USAGE(0x10000000, _N.dataEncipherment),
        keyEncipherment = new TPMA_X509_KEY_USAGE(0x20000000, _N.keyEncipherment),
        nonrepudiation = new TPMA_X509_KEY_USAGE(0x40000000, _N.nonrepudiation),
        contentCommitment = new TPMA_X509_KEY_USAGE(0x40000000, _N.contentCommitment),
        digitalSignature = new TPMA_X509_KEY_USAGE(0x80000000, _N.digitalSignature);
    
    public TPMA_X509_KEY_USAGE (int value) { super(value, _ValueMap); }
    
    public TPMA_X509_KEY_USAGE (TPMA_X509_KEY_USAGE...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_X509_KEY_USAGE fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_X509_KEY_USAGE.class); }
    
    public static TPMA_X509_KEY_USAGE fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_X509_KEY_USAGE.class); }
    
    public static TPMA_X509_KEY_USAGE fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_X509_KEY_USAGE.class); }
    
    public TPMA_X509_KEY_USAGE._N asEnum() { return (TPMA_X509_KEY_USAGE._N)NameAsEnum; }
    
    public static Collection<TPMA_X509_KEY_USAGE> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_X509_KEY_USAGE attr) { return super.hasAttr(attr); }
    
    public TPMA_X509_KEY_USAGE maskAttr (TPMA_X509_KEY_USAGE attr) { return super.maskAttr(attr, _ValueMap, TPMA_X509_KEY_USAGE.class); }
    
    private TPMA_X509_KEY_USAGE (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_X509_KEY_USAGE (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

