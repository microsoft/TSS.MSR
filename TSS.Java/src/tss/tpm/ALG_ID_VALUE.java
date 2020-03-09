package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* Proxy constants for TPM_ALG_ID enum
*/
public final class ALG_ID_VALUE extends TpmEnum<ALG_ID_VALUE>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the ALG_ID_VALUE. qualifier.
    public enum _N {
        /**
        * should not occur
        */
        ERROR_VALUE,
        
        /**
        * an object type that contains an RSA key
        */
        FIRST_VALUE,
        
        /**
        * an object type that contains an RSA key
        */
        RSA_VALUE,
        
        /**
        * block cipher with various key sizes (Triple Data Encryption Algorithm, commonly called Triple Data Encryption Standard)
        */
        TDES_VALUE,
        
        /**
        * hash algorithm producing a 160-bit digest
        */
        SHA_VALUE,
        
        /**
        * redefinition for documentation consistency
        */
        SHA1_VALUE,
        
        /**
        * Hash Message Authentication Code (HMAC) algorithm
        */
        HMAC_VALUE,
        
        /**
        * block cipher with various key sizes
        */
        AES_VALUE,
        
        /**
        * hash-based mask-generation function
        */
        MGF1_VALUE,
        
        /**
        * an object type that may use XOR for encryption or an HMAC for signing and may also refer to a data object that is neither signing nor encrypting
        */
        KEYEDHASH_VALUE,
        
        /**
        * hash-based stream cipher
        */
        XOR_VALUE,
        
        /**
        * hash algorithm producing a 256-bit digest
        */
        SHA256_VALUE,
        
        /**
        * hash algorithm producing a 384-bit digest
        */
        SHA384_VALUE,
        
        /**
        * hash algorithm producing a 512-bit digest
        */
        SHA512_VALUE,
        
        /**
        * Indication that no algorithm is selected
        */
        NULL_VALUE,
        
        /**
        * hash algorithm producing a 256-bit digest
        */
        SM3_256_VALUE,
        
        /**
        * symmetric block cipher with 128 bit key
        */
        SM4_VALUE,
        
        /**
        * a signature algorithm defined in section 8.2 (RSASSA-PKCS1-v1_5)
        */
        RSASSA_VALUE,
        
        /**
        * a padding algorithm defined in section 7.2 (RSAES-PKCS1-v1_5)
        */
        RSAES_VALUE,
        
        /**
        * a signature algorithm defined in section 8.1 (RSASSA-PSS)
        */
        RSAPSS_VALUE,
        
        /**
        * a padding algorithm defined in Section 7.1 (RSAES_OAEP)
        */
        OAEP_VALUE,
        
        /**
        * signature algorithm using elliptic curve cryptography (ECC)
        */
        ECDSA_VALUE,
        
        /**
        * secret sharing using ECC Based on context, this can be either One-Pass Diffie-Hellman, C(1, 1, ECC CDH) defined in 6.2.2.2 or Full Unified Model C(2, 2, ECC CDH) defined in 6.1.1.2
        */
        ECDH_VALUE,
        
        /**
        * elliptic-curve based, anonymous signing scheme
        */
        ECDAA_VALUE,
        
        /**
        * depending on context, either an elliptic-curve-based signature algorithm, encryption algorithm, or key exchange protocol
        */
        SM2_VALUE,
        
        /**
        * elliptic-curve based Schnorr signature
        */
        ECSCHNORR_VALUE,
        
        /**
        * two-phase elliptic-curve key exchange C(2, 2, ECC MQV) Section 6.1.1.4
        */
        ECMQV_VALUE,
        
        /**
        * concatenation key derivation function (approved alternative 1) Section 5.8.1
        */
        KDF1_SP800_56A_VALUE,
        
        /**
        * key derivation function KDF2 Section 13.2
        */
        KDF2_VALUE,
        
        /**
        * a key derivation method SP800-108, Section 5.1 KDF in Counter Mode
        */
        KDF1_SP800_108_VALUE,
        
        /**
        * prime field ECC
        */
        ECC_VALUE,
        
        /**
        * the object type for a symmetric block cipher key
        */
        SYMCIPHER_VALUE,
        
        /**
        * symmetric block cipher with various key sizes
        */
        CAMELLIA_VALUE,
        
        /**
        * Hash algorithm producing a 256-bit digest
        */
        SHA3_256_VALUE,
        
        /**
        * Hash algorithm producing a 384-bit digest
        */
        SHA3_384_VALUE,
        
        /**
        * Hash algorithm producing a 512-bit digest
        */
        SHA3_512_VALUE,
        
        CMAC_VALUE,
        
        /**
        * Counter mode if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
        */
        CTR_VALUE,
        
        /**
        * Output Feedback mode if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
        */
        OFB_VALUE,
        
        /**
        * Cipher Block Chaining mode if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
        */
        CBC_VALUE,
        
        /**
        * Cipher Feedback mode if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
        */
        CFB_VALUE,
        
        /**
        * Electronic Codebook mode if implemented, all implemented symmetric block ciphers (S type) shall be capable of using this mode. NOTE This mode is not recommended for uses unless the key is frequently rotated such as in video codecs
        */
        ECB_VALUE,
        
        LAST_VALUE,
        
        /**
        * Phony alg ID to be used for the first union member with no selector
        */
        ANY_VALUE,
        
        /**
        * Phony alg ID to be used for the second union member with no selector
        */
        ANY2_VALUE
        
    }
    
    private static ValueMap<ALG_ID_VALUE> _ValueMap = new ValueMap<ALG_ID_VALUE>();
    
    public static final ALG_ID_VALUE
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        ERROR_VALUE = new ALG_ID_VALUE(0x0000, _N.ERROR_VALUE),
        FIRST_VALUE = new ALG_ID_VALUE(0x0001, _N.FIRST_VALUE),
        RSA_VALUE = new ALG_ID_VALUE(0x0001, _N.RSA_VALUE),
        TDES_VALUE = new ALG_ID_VALUE(0x0003, _N.TDES_VALUE),
        SHA_VALUE = new ALG_ID_VALUE(0x0004, _N.SHA_VALUE),
        SHA1_VALUE = new ALG_ID_VALUE(0x0004, _N.SHA1_VALUE),
        HMAC_VALUE = new ALG_ID_VALUE(0x0005, _N.HMAC_VALUE),
        AES_VALUE = new ALG_ID_VALUE(0x0006, _N.AES_VALUE),
        MGF1_VALUE = new ALG_ID_VALUE(0x0007, _N.MGF1_VALUE),
        KEYEDHASH_VALUE = new ALG_ID_VALUE(0x0008, _N.KEYEDHASH_VALUE),
        XOR_VALUE = new ALG_ID_VALUE(0x000A, _N.XOR_VALUE),
        SHA256_VALUE = new ALG_ID_VALUE(0x000B, _N.SHA256_VALUE),
        SHA384_VALUE = new ALG_ID_VALUE(0x000C, _N.SHA384_VALUE),
        SHA512_VALUE = new ALG_ID_VALUE(0x000D, _N.SHA512_VALUE),
        NULL_VALUE = new ALG_ID_VALUE(0x0010, _N.NULL_VALUE),
        SM3_256_VALUE = new ALG_ID_VALUE(0x0012, _N.SM3_256_VALUE),
        SM4_VALUE = new ALG_ID_VALUE(0x0013, _N.SM4_VALUE),
        RSASSA_VALUE = new ALG_ID_VALUE(0x0014, _N.RSASSA_VALUE),
        RSAES_VALUE = new ALG_ID_VALUE(0x0015, _N.RSAES_VALUE),
        RSAPSS_VALUE = new ALG_ID_VALUE(0x0016, _N.RSAPSS_VALUE),
        OAEP_VALUE = new ALG_ID_VALUE(0x0017, _N.OAEP_VALUE),
        ECDSA_VALUE = new ALG_ID_VALUE(0x0018, _N.ECDSA_VALUE),
        ECDH_VALUE = new ALG_ID_VALUE(0x0019, _N.ECDH_VALUE),
        ECDAA_VALUE = new ALG_ID_VALUE(0x001A, _N.ECDAA_VALUE),
        SM2_VALUE = new ALG_ID_VALUE(0x001B, _N.SM2_VALUE),
        ECSCHNORR_VALUE = new ALG_ID_VALUE(0x001C, _N.ECSCHNORR_VALUE),
        ECMQV_VALUE = new ALG_ID_VALUE(0x001D, _N.ECMQV_VALUE),
        KDF1_SP800_56A_VALUE = new ALG_ID_VALUE(0x0020, _N.KDF1_SP800_56A_VALUE),
        KDF2_VALUE = new ALG_ID_VALUE(0x0021, _N.KDF2_VALUE),
        KDF1_SP800_108_VALUE = new ALG_ID_VALUE(0x0022, _N.KDF1_SP800_108_VALUE),
        ECC_VALUE = new ALG_ID_VALUE(0x0023, _N.ECC_VALUE),
        SYMCIPHER_VALUE = new ALG_ID_VALUE(0x0025, _N.SYMCIPHER_VALUE),
        CAMELLIA_VALUE = new ALG_ID_VALUE(0x0026, _N.CAMELLIA_VALUE),
        SHA3_256_VALUE = new ALG_ID_VALUE(0x0027, _N.SHA3_256_VALUE),
        SHA3_384_VALUE = new ALG_ID_VALUE(0x0028, _N.SHA3_384_VALUE),
        SHA3_512_VALUE = new ALG_ID_VALUE(0x0029, _N.SHA3_512_VALUE),
        CMAC_VALUE = new ALG_ID_VALUE(0x003F, _N.CMAC_VALUE),
        CTR_VALUE = new ALG_ID_VALUE(0x0040, _N.CTR_VALUE),
        OFB_VALUE = new ALG_ID_VALUE(0x0041, _N.OFB_VALUE),
        CBC_VALUE = new ALG_ID_VALUE(0x0042, _N.CBC_VALUE),
        CFB_VALUE = new ALG_ID_VALUE(0x0043, _N.CFB_VALUE),
        ECB_VALUE = new ALG_ID_VALUE(0x0044, _N.ECB_VALUE),
        LAST_VALUE = new ALG_ID_VALUE(0x0044, _N.LAST_VALUE),
        ANY_VALUE = new ALG_ID_VALUE(0x7FFF, _N.ANY_VALUE),
        ANY2_VALUE = new ALG_ID_VALUE(0x7FFE, _N.ANY2_VALUE);
    public ALG_ID_VALUE (int value) { super(value, _ValueMap); }
    
    public static ALG_ID_VALUE fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, ALG_ID_VALUE.class); }
    
    public static ALG_ID_VALUE fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, ALG_ID_VALUE.class); }
    
    public static ALG_ID_VALUE fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, ALG_ID_VALUE.class); }
    
    public ALG_ID_VALUE._N asEnum() { return (ALG_ID_VALUE._N)NameAsEnum; }
    
    public static Collection<ALG_ID_VALUE> values() { return _ValueMap.values(); }
    
    private ALG_ID_VALUE (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private ALG_ID_VALUE (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 2; }
}

//<<<

