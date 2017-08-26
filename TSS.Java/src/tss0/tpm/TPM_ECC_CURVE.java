package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 3 is the list of identifiers for TCG-registered curve ID values for elliptic curve cryptography.
*/
public final class TPM_ECC_CURVE extends TpmEnum<TPM_ECC_CURVE>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_ECC_CURVE. qualifier.
    public enum _N {
        NONE,
        
        NIST_P192,
        
        NIST_P224,
        
        NIST_P256,
        
        NIST_P384,
        
        NIST_P521,
        
        /**
        * curve to support ECDAA
        */
        BN_P256,
        
        /**
        * curve to support ECDAA
        */
        BN_P638,
        
        SM2_P256
        
    }
    
    private static ValueMap<TPM_ECC_CURVE> _ValueMap = new ValueMap<TPM_ECC_CURVE>();
    
    public static final TPM_ECC_CURVE
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        NONE = new TPM_ECC_CURVE(0x0000, _N.NONE),
        NIST_P192 = new TPM_ECC_CURVE(0x0001, _N.NIST_P192),
        NIST_P224 = new TPM_ECC_CURVE(0x0002, _N.NIST_P224),
        NIST_P256 = new TPM_ECC_CURVE(0x0003, _N.NIST_P256),
        NIST_P384 = new TPM_ECC_CURVE(0x0004, _N.NIST_P384),
        NIST_P521 = new TPM_ECC_CURVE(0x0005, _N.NIST_P521),
        BN_P256 = new TPM_ECC_CURVE(0x0010, _N.BN_P256),
        BN_P638 = new TPM_ECC_CURVE(0x0011, _N.BN_P638),
        SM2_P256 = new TPM_ECC_CURVE(0x0020, _N.SM2_P256);
    public TPM_ECC_CURVE (int value) { super(value, _ValueMap); }
    
    public static TPM_ECC_CURVE fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_ECC_CURVE.class); }
    
    public static TPM_ECC_CURVE fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_ECC_CURVE.class); }
    
    public static TPM_ECC_CURVE fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_ECC_CURVE.class); }
    
    public TPM_ECC_CURVE._N asEnum() { return (TPM_ECC_CURVE._N)NameAsEnum; }
    
    public static Collection<TPM_ECC_CURVE> values() { return _ValueMap.values(); }
    
    private TPM_ECC_CURVE (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_ECC_CURVE (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 2; }
}

//<<<

