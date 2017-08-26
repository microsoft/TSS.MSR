package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 2 is the list of algorithms to which the TCG has assigned an algorithm identifier along with its numeric identifier.
*/
public final class TPM_ALG_ID extends TpmEnum<TPM_ALG_ID>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_ALG_ID. qualifier.
    public enum _N {
        /**
        * should not occur
        */
        ERROR,
        
        /**
        * an object type that contains an RSA key
        */
        FIRST,
        
        /**
        * an object type that contains an RSA key
        */
        RSA,
        
        TDES,
        
        /**
        * hash algorithm producing a 160-bit digest
        */
        SHA,
        
        /**
        * redefinition for documentation consistency
        */
        SHA1,
        
        /**
        * Hash Message Authentication Code (HMAC) algorithm
        */
        HMAC,
        
        /**
        * block cipher with various key sizes
        */
        AES,
        
        /**
        * hash-based mask-generation function
        */
        MGF1,
        
        /**
        * an object type that may use XOR for encryption or an HMAC for signing and may also refer to a data object that is neither signing nor encrypting
        */
        KEYEDHASH,
        
        /**
        * hash-based stream cipher
        */
        XOR,
        
        /**
        * hash algorithm producing a 256-bit digest
        */
        SHA256,
        
        /**
        * hash algorithm producing a 384-bit digest
        */
        SHA384,
        
        /**
        * hash algorithm producing a 512-bit digest
        */
        SHA512,
        
        /**
        * Indication that no algorithm is selected
        */
        NULL,
        
        /**
        * hash algorithm producing a 256-bit digest
        */
        SM3_256,
        
        /**
        * symmetric block cipher with 128 bit key
        */
        SM4,
        
        /**
        * a signature algorithm defined in section 8.2 (RSASSA-PKCS1-v1_5)
        */
        RSASSA,
        
        /**
        * a padding algorithm defined in section 7.2 (RSAES-PKCS1-v1_5)
        */
        RSAES,
        
        /**
        * a signature algorithm defined in section 8.1 (RSASSA-PSS)
        */
        RSAPSS,
        
        /**
        * a padding algorithm defined in Section 7.1 (RSAES_OAEP)
        */
        OAEP,
        
        /**
        * signature algorithm using elliptic curve cryptography (ECC)
        */
        ECDSA,
        
        /**
        * secret sharing using ECC Based on context, this can be either One-Pass Diffie-Hellman, C(1, 1, ECC CDH) defined in 6.2.2.2 or Full Unified Model C(2, 2, ECC CDH) defined in 6.1.1.2
        */
        ECDH,
        
        /**
        * elliptic-curve based, anonymous signing scheme
        */
        ECDAA,
        
        /**
        * depending on context, either an elliptic-curve based, signature algorithm or a key exchange protocol NOTE Type listed as signing but, other uses are allowed according to context.
        */
        SM2,
        
        /**
        * elliptic-curve based Schnorr signature
        */
        ECSCHNORR,
        
        /**
        * two-phase elliptic-curve key exchange C(2, 2, ECC MQV) Section 6.1.1.4
        */
        ECMQV,
        
        /**
        * concatenation key derivation function (approved alternative 1) Section 5.8.1
        */
        KDF1_SP800_56A,
        
        /**
        * key derivation function KDF2 Section 13.2
        */
        KDF2,
        
        /**
        * a key derivation method SP800-108, Section 5.1 KDF in Counter Mode
        */
        KDF1_SP800_108,
        
        /**
        * prime field ECC
        */
        ECC,
        
        /**
        * the object type for a symmetric block cipher key
        */
        SYMCIPHER,
        
        /**
        * symmetric block cipher with various key sizes
        */
        CAMELLIA,
        
        CMAC,
        
        /**
        * Counter mode if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
        */
        CTR,
        
        /**
        * Output Feedback mode if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
        */
        OFB,
        
        /**
        * Cipher Block Chaining mode if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
        */
        CBC,
        
        /**
        * Cipher Feedback mode if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
        */
        CFB,
        
        /**
        * Electronic Codebook mode if implemented, all implemented symmetric block ciphers (S type) shall be capable of using this mode. NOTE This mode is not recommended for uses unless the key is frequently rotated such as in video codecs
        */
        ECB,
        
        LAST,
        
        /**
        * Phony alg ID to be used for the first union member with no selector
        */
        ANY,
        
        /**
        * Phony alg ID to be used for the second union member with no selector
        */
        ANY2
        
    }
    
    private static ValueMap<TPM_ALG_ID> _ValueMap = new ValueMap<TPM_ALG_ID>();
    
    public static final TPM_ALG_ID
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        ERROR = new TPM_ALG_ID(0x0000, _N.ERROR),
        FIRST = new TPM_ALG_ID(0x0001, _N.FIRST),
        RSA = new TPM_ALG_ID(0x0001, _N.RSA),
        TDES = new TPM_ALG_ID(0x0003, _N.TDES),
        SHA = new TPM_ALG_ID(0x0004, _N.SHA),
        SHA1 = new TPM_ALG_ID(0x0004, _N.SHA1),
        HMAC = new TPM_ALG_ID(0x0005, _N.HMAC),
        AES = new TPM_ALG_ID(0x0006, _N.AES),
        MGF1 = new TPM_ALG_ID(0x0007, _N.MGF1),
        KEYEDHASH = new TPM_ALG_ID(0x0008, _N.KEYEDHASH),
        XOR = new TPM_ALG_ID(0x000A, _N.XOR),
        SHA256 = new TPM_ALG_ID(0x000B, _N.SHA256),
        SHA384 = new TPM_ALG_ID(0x000C, _N.SHA384),
        SHA512 = new TPM_ALG_ID(0x000D, _N.SHA512),
        NULL = new TPM_ALG_ID(0x0010, _N.NULL),
        SM3_256 = new TPM_ALG_ID(0x0012, _N.SM3_256),
        SM4 = new TPM_ALG_ID(0x0013, _N.SM4),
        RSASSA = new TPM_ALG_ID(0x0014, _N.RSASSA),
        RSAES = new TPM_ALG_ID(0x0015, _N.RSAES),
        RSAPSS = new TPM_ALG_ID(0x0016, _N.RSAPSS),
        OAEP = new TPM_ALG_ID(0x0017, _N.OAEP),
        ECDSA = new TPM_ALG_ID(0x0018, _N.ECDSA),
        ECDH = new TPM_ALG_ID(0x0019, _N.ECDH),
        ECDAA = new TPM_ALG_ID(0x001A, _N.ECDAA),
        SM2 = new TPM_ALG_ID(0x001B, _N.SM2),
        ECSCHNORR = new TPM_ALG_ID(0x001C, _N.ECSCHNORR),
        ECMQV = new TPM_ALG_ID(0x001D, _N.ECMQV),
        KDF1_SP800_56A = new TPM_ALG_ID(0x0020, _N.KDF1_SP800_56A),
        KDF2 = new TPM_ALG_ID(0x0021, _N.KDF2),
        KDF1_SP800_108 = new TPM_ALG_ID(0x0022, _N.KDF1_SP800_108),
        ECC = new TPM_ALG_ID(0x0023, _N.ECC),
        SYMCIPHER = new TPM_ALG_ID(0x0025, _N.SYMCIPHER),
        CAMELLIA = new TPM_ALG_ID(0x0026, _N.CAMELLIA),
        CMAC = new TPM_ALG_ID(0x003F, _N.CMAC),
        CTR = new TPM_ALG_ID(0x0040, _N.CTR),
        OFB = new TPM_ALG_ID(0x0041, _N.OFB),
        CBC = new TPM_ALG_ID(0x0042, _N.CBC),
        CFB = new TPM_ALG_ID(0x0043, _N.CFB),
        ECB = new TPM_ALG_ID(0x0044, _N.ECB),
        LAST = new TPM_ALG_ID(0x0044, _N.LAST, true),
        ANY = new TPM_ALG_ID(0x7FFF, _N.ANY),
        ANY2 = new TPM_ALG_ID(0x7FFE, _N.ANY2);
    public TPM_ALG_ID (int value) { super(value, _ValueMap); }
    
    public static TPM_ALG_ID fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_ALG_ID.class); }
    
    public static TPM_ALG_ID fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_ALG_ID.class); }
    
    public static TPM_ALG_ID fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_ALG_ID.class); }
    
    public TPM_ALG_ID._N asEnum() { return (TPM_ALG_ID._N)NameAsEnum; }
    
    public static Collection<TPM_ALG_ID> values() { return _ValueMap.values(); }
    
    private TPM_ALG_ID (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_ALG_ID (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 2; }
}

//<<<

