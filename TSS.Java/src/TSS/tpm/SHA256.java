package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 13 Defines for SHA256 Hash Values
*/
public final class SHA256 extends TpmEnum<SHA256>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the SHA256. qualifier.
    public enum _N {
        /**
        * size of digest
        */
        DIGEST_SIZE,
        
        /**
        * size of hash block
        */
        BLOCK_SIZE,
        
        /**
        * size of the DER in octets
        */
        DER_SIZE
        
    }
    
    private static ValueMap<SHA256> _ValueMap = new ValueMap<SHA256>();
    
    public static final SHA256
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        DIGEST_SIZE = new SHA256(32, _N.DIGEST_SIZE),
        BLOCK_SIZE = new SHA256(64, _N.BLOCK_SIZE),
        DER_SIZE = new SHA256(19, _N.DER_SIZE);
    public SHA256 (int value) { super(value, _ValueMap); }
    
    public static SHA256 fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, SHA256.class); }
    
    public static SHA256 fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, SHA256.class); }
    
    public static SHA256 fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, SHA256.class); }
    
    public SHA256._N asEnum() { return (SHA256._N)NameAsEnum; }
    
    public static Collection<SHA256> values() { return _ValueMap.values(); }
    
    private SHA256 (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private SHA256 (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

