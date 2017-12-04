package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Contains the public and the plaintext-sensitive and/or encrypted private part of a TPM key (or other object)
*/
public class TssObject extends TpmStructure
{
    /**
     * Contains the public and the plaintext-sensitive and/or encrypted private part of a TPM key (or other object)
     * 
     * @param _Public Public part of key 
     * @param _Sensitive Sensitive part of key 
     * @param _Private Private part is the encrypted sensitive part of key
     */
    public TssObject(TPMT_PUBLIC _Public,TPMT_SENSITIVE _Sensitive,TPM2B_PRIVATE _Private)
    {
        Public = _Public;
        Sensitive = _Sensitive;
        Private = _Private;
    }
    /**
    * Contains the public and the plaintext-sensitive and/or encrypted private part of a TPM key (or other object)
    */
    public TssObject() {};
    /**
    * Public part of key
    */
    public TPMT_PUBLIC Public;
    /**
    * Sensitive part of key
    */
    public TPMT_SENSITIVE Sensitive;
    /**
    * Private part is the encrypted sensitive part of key
    */
    public TPM2B_PRIVATE Private;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        Public.toTpm(buf);
        Sensitive.toTpm(buf);
        Private.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        Public = TPMT_PUBLIC.fromTpm(buf);
        Sensitive = TPMT_SENSITIVE.fromTpm(buf);
        Private = TPM2B_PRIVATE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TssObject fromTpm (byte[] x) 
    {
        TssObject ret = new TssObject();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TssObject fromTpm (InByteBuf buf) 
    {
        TssObject ret = new TssObject();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TssObject");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_PUBLIC", "Public", Public);
        _p.add(d, "TPMT_SENSITIVE", "Sensitive", Sensitive);
        _p.add(d, "TPM2B_PRIVATE", "Private", Private);
    };
    
    
};

//<<<

