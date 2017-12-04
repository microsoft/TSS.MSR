package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 175 Definition of {RSA} TPMS_SIGNATURE_RSA Structure
*/
public class TPMS_SIGNATURE_RSAPSS extends TpmStructure implements TPMU_SIGNATURE 
{
    /**
     * Table 175 Definition of {RSA} TPMS_SIGNATURE_RSA Structure
     * 
     * @param _hash the hash algorithm used to digest the message TPM_ALG_NULL is not allowed. 
     * @param _sig The signature is the size of a public key.
     */
    public TPMS_SIGNATURE_RSAPSS(TPM_ALG_ID _hash,byte[] _sig)
    {
        hash = _hash;
        sig = _sig;
    }
    /**
    * Table 175 Definition of {RSA} TPMS_SIGNATURE_RSA Structure
    */
    public TPMS_SIGNATURE_RSAPSS() {};
    /**
    * the hash algorithm used to digest the message TPM_ALG_NULL is not allowed.
    */
    public TPM_ALG_ID hash;
    /**
    * size of the buffer The value of zero is only valid for create.
    */
    // private short sigSize;
    /**
    * The signature is the size of a public key.
    */
    public byte[] sig;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        hash.toTpm(buf);
        buf.writeInt((sig!=null)?sig.length:0, 2);
        if(sig!=null)
            buf.write(sig);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        hash = TPM_ALG_ID.fromTpm(buf);
        int _sigSize = buf.readInt(2);
        sig = new byte[_sigSize];
        buf.readArrayOfInts(sig, 1, _sigSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_SIGNATURE_RSAPSS fromTpm (byte[] x) 
    {
        TPMS_SIGNATURE_RSAPSS ret = new TPMS_SIGNATURE_RSAPSS();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_SIGNATURE_RSAPSS fromTpm (InByteBuf buf) 
    {
        TPMS_SIGNATURE_RSAPSS ret = new TPMS_SIGNATURE_RSAPSS();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIGNATURE_RSAPSS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hash", hash);
        _p.add(d, "byte", "sig", sig);
    };
    
    
};

//<<<

