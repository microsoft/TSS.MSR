package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 14 Defines for SHA384 Hash Values
*/
public final class SHA384 extends TpmEnum<SHA384>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the SHA384. qualifier.
    public enum _N {
        /**
        * size of digest in octets
        */
        DIGEST_SIZE,
        
        /**
        * size of hash block in octets
        */
        BLOCK_SIZE,
        
        /**
        * size of the DER in octets
        */
        DER_SIZE
        
    }
    
    private static ValueMap<SHA384> _ValueMap = new ValueMap<SHA384>();
    
    public static final SHA384
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        DIGEST_SIZE = new SHA384(48, _N.DIGEST_SIZE),
        BLOCK_SIZE = new SHA384(128, _N.BLOCK_SIZE),
        DER_SIZE = new SHA384(19, _N.DER_SIZE);
    public SHA384 (int value) { super(value, _ValueMap); }
    
    public static SHA384 fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, SHA384.class); }
    
    public static SHA384 fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, SHA384.class); }
    
    public static SHA384 fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, SHA384.class); }
    
    public SHA384._N asEnum() { return (SHA384._N)NameAsEnum; }
    
    public static Collection<SHA384> values() { return _ValueMap.values(); }
    
    private SHA384 (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private SHA384 (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

