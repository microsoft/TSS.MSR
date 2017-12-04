package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This list is used to convey a list of digest values. This type is used in TPM2_PolicyOR() and in TPM2_PCR_Read().
*/
public class TPML_DIGEST extends TpmStructure
{
    /**
     * This list is used to convey a list of digest values. This type is used in TPM2_PolicyOR() and in TPM2_PCR_Read().
     * 
     * @param _digests a list of digests For TPM2_PolicyOR(), all digests will have been computed using the digest of the policy session. For TPM2_PCR_Read(), each digest will be the size of the digest for the bank containing the PCR.
     */
    public TPML_DIGEST(TPM2B_DIGEST[] _digests)
    {
        digests = _digests;
    }
    /**
    * This list is used to convey a list of digest values. This type is used in TPM2_PolicyOR() and in TPM2_PCR_Read().
    */
    public TPML_DIGEST() {};
    /**
    * number of digests in the list, minimum is two for TPM2_PolicyOR().
    */
    // private int count;
    /**
    * a list of digests For TPM2_PolicyOR(), all digests will have been computed using the digest of the policy session. For TPM2_PCR_Read(), each digest will be the size of the digest for the bank containing the PCR.
    */
    public TPM2B_DIGEST[] digests;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((digests!=null)?digests.length:0, 4);
        if(digests!=null)
            buf.writeArrayOfTpmObjects(digests);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _count = buf.readInt(4);
        digests = new TPM2B_DIGEST[_count];
        for(int j=0;j<_count;j++)digests[j]=new TPM2B_DIGEST();
        buf.readArrayOfTpmObjects(digests, _count);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPML_DIGEST fromTpm (byte[] x) 
    {
        TPML_DIGEST ret = new TPML_DIGEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPML_DIGEST fromTpm (InByteBuf buf) 
    {
        TPML_DIGEST ret = new TPML_DIGEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_DIGEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM2B_DIGEST", "digests", digests);
    };
    
    
};

//<<<

