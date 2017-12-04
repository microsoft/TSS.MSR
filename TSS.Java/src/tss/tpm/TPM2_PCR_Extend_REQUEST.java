package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to cause an update to the indicated PCR. The digests parameter contains one or more tagged digest values identified by an algorithm ID. For each digest, the PCR associated with pcrHandle is Extended into the bank identified by the tag (hashAlg).
*/
public class TPM2_PCR_Extend_REQUEST extends TpmStructure
{
    /**
     * This command is used to cause an update to the indicated PCR. The digests parameter contains one or more tagged digest values identified by an algorithm ID. For each digest, the PCR associated with pcrHandle is Extended into the bank identified by the tag (hashAlg).
     * 
     * @param _pcrHandle handle of the PCR Auth Handle: 1 Auth Role: USER 
     * @param _digests list of tagged digest values to be extended
     */
    public TPM2_PCR_Extend_REQUEST(TPM_HANDLE _pcrHandle,TPMT_HA[] _digests)
    {
        pcrHandle = _pcrHandle;
        digests = _digests;
    }
    /**
    * This command is used to cause an update to the indicated PCR. The digests parameter contains one or more tagged digest values identified by an algorithm ID. For each digest, the PCR associated with pcrHandle is Extended into the bank identified by the tag (hashAlg).
    */
    public TPM2_PCR_Extend_REQUEST() {};
    /**
    * handle of the PCR Auth Handle: 1 Auth Role: USER
    */
    public TPM_HANDLE pcrHandle;
    /**
    * number of digests in the list
    */
    // private int digestsCount;
    /**
    * list of tagged digest values to be extended
    */
    public TPMT_HA[] digests;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        pcrHandle.toTpm(buf);
        buf.writeInt((digests!=null)?digests.length:0, 4);
        if(digests!=null)
            buf.writeArrayOfTpmObjects(digests);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        pcrHandle = TPM_HANDLE.fromTpm(buf);
        int _digestsCount = buf.readInt(4);
        digests = new TPMT_HA[_digestsCount];
        for(int j=0;j<_digestsCount;j++)digests[j]=new TPMT_HA();
        buf.readArrayOfTpmObjects(digests, _digestsCount);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PCR_Extend_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PCR_Extend_REQUEST ret = new TPM2_PCR_Extend_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PCR_Extend_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PCR_Extend_REQUEST ret = new TPM2_PCR_Extend_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Extend_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "pcrHandle", pcrHandle);
        _p.add(d, "TpmHash", "digests", digests);
    };
    
    
};

//<<<

