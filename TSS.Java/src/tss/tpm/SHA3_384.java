package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>

/** Table 19 Defines for SHA3_384 Hash Values */
public final class SHA3_384 extends TpmEnum<SHA3_384>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the SHA3_384. qualifier.
    public enum _N {
        /** size of digest in octets */
        DIGEST_SIZE,
        
        /** size of hash block in octets */
        BLOCK_SIZE
    }

    private static ValueMap<SHA3_384> _ValueMap = new ValueMap<SHA3_384>();
    
    /** These definitions provide mapping of the Java enum constants to their TPM integer values */
    public static final SHA3_384
        DIGEST_SIZE = new SHA3_384(48, _N.DIGEST_SIZE),
        BLOCK_SIZE = new SHA3_384(104, _N.BLOCK_SIZE);
    
    public SHA3_384 (int value) { super(value, _ValueMap); }
    
    public static SHA3_384 fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, SHA3_384.class); }
    
    public static SHA3_384 fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, SHA3_384.class); }
    
    public static SHA3_384 fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, SHA3_384.class); }
    
    public SHA3_384._N asEnum() { return (SHA3_384._N)NameAsEnum; }
    
    public static Collection<SHA3_384> values() { return _ValueMap.values(); }
    
    private SHA3_384 (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private SHA3_384 (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<
