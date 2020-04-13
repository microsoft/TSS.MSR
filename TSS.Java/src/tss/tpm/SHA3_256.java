package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>

/** Table 18 Defines for SHA3_256 Hash Values */
public final class SHA3_256 extends TpmEnum<SHA3_256>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the SHA3_256. qualifier.
    public enum _N {
        /** size of digest in octets */
        DIGEST_SIZE,
        
        /** size of hash block in octets */
        BLOCK_SIZE
    }

    private static ValueMap<SHA3_256> _ValueMap = new ValueMap<SHA3_256>();
    
    /** These definitions provide mapping of the Java enum constants to their TPM integer values */
    public static final SHA3_256
        DIGEST_SIZE = new SHA3_256(32, _N.DIGEST_SIZE),
        BLOCK_SIZE = new SHA3_256(136, _N.BLOCK_SIZE);
    
    public SHA3_256 (int value) { super(value, _ValueMap); }
    
    public static SHA3_256 fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, SHA3_256.class); }
    
    public static SHA3_256 fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, SHA3_256.class); }
    
    public static SHA3_256 fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, SHA3_256.class); }
    
    public SHA3_256._N asEnum() { return (SHA3_256._N)NameAsEnum; }
    
    public static Collection<SHA3_256> values() { return _ValueMap.values(); }
    
    private SHA3_256 (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private SHA3_256 (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

