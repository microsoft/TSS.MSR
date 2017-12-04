package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is for the XOR encryption scheme.
*/
public class TPMS_SCHEME_XOR extends TpmStructure implements TPMU_SCHEME_KEYEDHASH 
{
    /**
     * This structure is for the XOR encryption scheme.
     * 
     * @param _hashAlg the hash algorithm used to digest the message 
     * @param _kdf the key derivation function
     */
    public TPMS_SCHEME_XOR(TPM_ALG_ID _hashAlg,TPM_ALG_ID _kdf)
    {
        hashAlg = _hashAlg;
        kdf = _kdf;
    }
    /**
    * This structure is for the XOR encryption scheme.
    */
    public TPMS_SCHEME_XOR() {};
    /**
    * the hash algorithm used to digest the message
    */
    public TPM_ALG_ID hashAlg;
    /**
    * the key derivation function
    */
    public TPM_ALG_ID kdf;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        hashAlg.toTpm(buf);
        kdf.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        kdf = TPM_ALG_ID.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_SCHEME_XOR fromTpm (byte[] x) 
    {
        TPMS_SCHEME_XOR ret = new TPMS_SCHEME_XOR();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_SCHEME_XOR fromTpm (InByteBuf buf) 
    {
        TPMS_SCHEME_XOR ret = new TPMS_SCHEME_XOR();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_XOR");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
        _p.add(d, "TPM_ALG_ID", "kdf", kdf);
    };
    
    
};

//<<<

