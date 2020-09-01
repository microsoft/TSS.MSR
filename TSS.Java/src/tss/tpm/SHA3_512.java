package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>

/** Table 20 Defines for SHA3_512 Hash Values  */
public final class SHA3_512 extends TpmEnum<SHA3_512>
{
    /** Values from enum _N are only intended to be used in case labels of a switch statement
     *  using the result of this.asEnum() method as the switch condition. However, their Java
     *  names are identical to those of the constants defined in this class further below, so
     *  for any other usage just prepend them with the SHA3_512. qualifier.
     */
    public enum _N {
        /** Size of digest in octets  */
        DIGEST_SIZE,

        /** Size of hash block in octets  */
        BLOCK_SIZE
    }

    private static ValueMap<SHA3_512> _ValueMap = new ValueMap<SHA3_512>();

    /** These definitions provide mapping of the Java enum constants to their TPM integer values  */
    public static final SHA3_512
        DIGEST_SIZE = new SHA3_512(64, _N.DIGEST_SIZE),
        BLOCK_SIZE = new SHA3_512(72, _N.BLOCK_SIZE);

    public SHA3_512 () { super(0, _ValueMap); }
    public SHA3_512 (int value) { super(value, _ValueMap); }
    public static SHA3_512 fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, SHA3_512.class); }
    public static SHA3_512 fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, SHA3_512.class); }
    public static SHA3_512 fromTpm (TpmBuffer buf) { return TpmEnum.fromTpm(buf, _ValueMap, SHA3_512.class); }
    public SHA3_512._N asEnum() { return (SHA3_512._N)NameAsEnum; }
    public static Collection<SHA3_512> values() { return _ValueMap.values(); }
    private SHA3_512 (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    private SHA3_512 (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }

    @Override
    protected int wireSize() { return 4; }
}

//<<<
