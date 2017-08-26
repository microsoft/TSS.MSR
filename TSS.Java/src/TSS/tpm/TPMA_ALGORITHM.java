package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure defines the attributes of an algorithm.
*/
public final class TPMA_ALGORITHM extends TpmAttribute<TPMA_ALGORITHM>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_ALGORITHM. qualifier.
    public enum _N {
        /**
        * SET (1): an asymmetric algorithm with public and private portions CLEAR (0): not an asymmetric algorithm
        */
        asymmetric,
        /**
        * SET (1): a symmetric block cipher CLEAR (0): not a symmetric block cipher
        */
        symmetric,
        /**
        * SET (1): a hash algorithm CLEAR (0): not a hash algorithm
        */
        hash,
        /**
        * SET (1): an algorithm that may be used as an object type CLEAR (0): an algorithm that is not used as an object type
        */
        object,
        /**
        * SET (1): a signing algorithm. The setting of asymmetric, symmetric, and hash will indicate the type of signing algorithm. CLEAR (0): not a signing algorithm
        */
        signing,
        /**
        * SET (1): an encryption/decryption algorithm. The setting of asymmetric, symmetric, and hash will indicate the type of encryption/decryption algorithm. CLEAR (0): not an encryption/decryption algorithm
        */
        encrypting,
        /**
        * SET (1): a method such as a key derivative function (KDF) CLEAR (0): not a method
        */
        method
    }
    
    private static ValueMap<TPMA_ALGORITHM>	_ValueMap = new ValueMap<TPMA_ALGORITHM>();
    
    public static final TPMA_ALGORITHM
    
        asymmetric = new TPMA_ALGORITHM(0x1, _N.asymmetric),
        symmetric = new TPMA_ALGORITHM(0x2, _N.symmetric),
        hash = new TPMA_ALGORITHM(0x4, _N.hash),
        object = new TPMA_ALGORITHM(0x8, _N.object),
        signing = new TPMA_ALGORITHM(0x100, _N.signing),
        encrypting = new TPMA_ALGORITHM(0x200, _N.encrypting),
        method = new TPMA_ALGORITHM(0x400, _N.method);
    public TPMA_ALGORITHM (int value) { super(value, _ValueMap); }
    
    public TPMA_ALGORITHM (TPMA_ALGORITHM...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_ALGORITHM fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_ALGORITHM.class); }
    
    public static TPMA_ALGORITHM fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_ALGORITHM.class); }
    
    public static TPMA_ALGORITHM fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_ALGORITHM.class); }
    
    public TPMA_ALGORITHM._N asEnum() { return (TPMA_ALGORITHM._N)NameAsEnum; }
    
    public static Collection<TPMA_ALGORITHM> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_ALGORITHM attr) { return super.hasAttr(attr); }
    
    public TPMA_ALGORITHM maskAttr (TPMA_ALGORITHM attr) { return super.maskAttr(attr, _ValueMap, TPMA_ALGORITHM.class); }
    
    private TPMA_ALGORITHM (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_ALGORITHM (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

