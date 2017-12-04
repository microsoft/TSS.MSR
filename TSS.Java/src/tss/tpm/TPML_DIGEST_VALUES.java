package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This list is used to convey a list of digest values. This type is returned by TPM2_Event() and TPM2_SequenceComplete() and is an input for TPM2_PCR_Extend().
*/
public class TPML_DIGEST_VALUES extends TpmStructure
{
    /**
     * This list is used to convey a list of digest values. This type is returned by TPM2_Event() and TPM2_SequenceComplete() and is an input for TPM2_PCR_Extend().
     * 
     * @param _digests a list of tagged digests
     */
    public TPML_DIGEST_VALUES(TPMT_HA[] _digests)
    {
        digests = _digests;
    }
    /**
    * This list is used to convey a list of digest values. This type is returned by TPM2_Event() and TPM2_SequenceComplete() and is an input for TPM2_PCR_Extend().
    */
    public TPML_DIGEST_VALUES() {};
    /**
    * number of digests in the list
    */
    // private int count;
    /**
    * a list of tagged digests
    */
    public TPMT_HA[] digests;
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
        digests = new TPMT_HA[_count];
        for(int j=0;j<_count;j++)digests[j]=new TPMT_HA();
        buf.readArrayOfTpmObjects(digests, _count);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPML_DIGEST_VALUES fromTpm (byte[] x) 
    {
        TPML_DIGEST_VALUES ret = new TPML_DIGEST_VALUES();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPML_DIGEST_VALUES fromTpm (InByteBuf buf) 
    {
        TPML_DIGEST_VALUES ret = new TPML_DIGEST_VALUES();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_DIGEST_VALUES");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TpmHash", "digests", digests);
    };
    
    
};

//<<<

