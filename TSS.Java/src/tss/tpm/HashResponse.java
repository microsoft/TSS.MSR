package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command performs a hash operation on a data buffer and returns the results.
*/
public class HashResponse extends TpmStructure
{
    /**
     * This command performs a hash operation on a data buffer and returns the results.
     * 
     * @param _outHash results 
     * @param _validation ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE will be a NULL ticket if the digest may not be signed with a restricted key
     */
    public HashResponse(byte[] _outHash,TPMT_TK_HASHCHECK _validation)
    {
        outHash = _outHash;
        validation = _validation;
    }
    /**
    * This command performs a hash operation on a data buffer and returns the results.
    */
    public HashResponse() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short outHashSize;
    /**
    * results
    */
    public byte[] outHash;
    /**
    * ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE will be a NULL ticket if the digest may not be signed with a restricted key
    */
    public TPMT_TK_HASHCHECK validation;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((outHash!=null)?outHash.length:0, 2);
        if(outHash!=null)
            buf.write(outHash);
        validation.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outHashSize = buf.readInt(2);
        outHash = new byte[_outHashSize];
        buf.readArrayOfInts(outHash, 1, _outHashSize);
        validation = TPMT_TK_HASHCHECK.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static HashResponse fromTpm (byte[] x) 
    {
        HashResponse ret = new HashResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static HashResponse fromTpm (InByteBuf buf) 
    {
        HashResponse ret = new HashResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Hash_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "outHash", outHash);
        _p.add(d, "TPMT_TK_HASHCHECK", "validation", validation);
    };
    
    
};

//<<<

