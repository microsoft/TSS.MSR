package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 18 Definition of (UINT16) TPM_EO Constants (IN/OUT)
*/
public final class TPM_EO extends TpmEnum<TPM_EO>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_EO. qualifier.
    public enum _N {
        /**
        * A = B
        */
        EQ,
        
        /**
        * A B
        */
        NEQ,
        
        /**
        * A ) B signed
        */
        SIGNED_GT,
        
        /**
        * A ) B unsigned
        */
        UNSIGNED_GT,
        
        /**
        * A ( B signed
        */
        SIGNED_LT,
        
        /**
        * A ( B unsigned
        */
        UNSIGNED_LT,
        
        /**
        * A B signed
        */
        SIGNED_GE,
        
        /**
        * A B unsigned
        */
        UNSIGNED_GE,
        
        /**
        * A B signed
        */
        SIGNED_LE,
        
        /**
        * A B unsigned
        */
        UNSIGNED_LE,
        
        /**
        * All bits SET in B are SET in A. ((A AND B)=B)
        */
        BITSET,
        
        /**
        * All bits SET in B are CLEAR in A. ((A AND B)=0)
        */
        BITCLEAR
        
    }
    
    private static ValueMap<TPM_EO> _ValueMap = new ValueMap<TPM_EO>();
    
    public static final TPM_EO
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        EQ = new TPM_EO(0x0000, _N.EQ),
        NEQ = new TPM_EO(0x0001, _N.NEQ),
        SIGNED_GT = new TPM_EO(0x0002, _N.SIGNED_GT),
        UNSIGNED_GT = new TPM_EO(0x0003, _N.UNSIGNED_GT),
        SIGNED_LT = new TPM_EO(0x0004, _N.SIGNED_LT),
        UNSIGNED_LT = new TPM_EO(0x0005, _N.UNSIGNED_LT),
        SIGNED_GE = new TPM_EO(0x0006, _N.SIGNED_GE),
        UNSIGNED_GE = new TPM_EO(0x0007, _N.UNSIGNED_GE),
        SIGNED_LE = new TPM_EO(0x0008, _N.SIGNED_LE),
        UNSIGNED_LE = new TPM_EO(0x0009, _N.UNSIGNED_LE),
        BITSET = new TPM_EO(0x000A, _N.BITSET),
        BITCLEAR = new TPM_EO(0x000B, _N.BITCLEAR);
    public TPM_EO (int value) { super(value, _ValueMap); }
    
    public static TPM_EO fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_EO.class); }
    
    public static TPM_EO fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_EO.class); }
    
    public static TPM_EO fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_EO.class); }
    
    public TPM_EO._N asEnum() { return (TPM_EO._N)NameAsEnum; }
    
    public static Collection<TPM_EO> values() { return _ValueMap.values(); }
    
    private TPM_EO (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_EO (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 2; }
}

//<<<

