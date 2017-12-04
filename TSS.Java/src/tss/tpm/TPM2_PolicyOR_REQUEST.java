package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows options in authorizations without requiring that the TPM evaluate all of the options. If a policy may be satisfied by different sets of conditions, the TPM need only evaluate one set that satisfies the policy. This command will indicate that one of the required sets of conditions has been satisfied.
*/
public class TPM2_PolicyOR_REQUEST extends TpmStructure
{
    /**
     * This command allows options in authorizations without requiring that the TPM evaluate all of the options. If a policy may be satisfied by different sets of conditions, the TPM need only evaluate one set that satisfies the policy. This command will indicate that one of the required sets of conditions has been satisfied.
     * 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _pHashList the list of hashes to check for a match
     */
    public TPM2_PolicyOR_REQUEST(TPM_HANDLE _policySession,TPM2B_DIGEST[] _pHashList)
    {
        policySession = _policySession;
        pHashList = _pHashList;
    }
    /**
    * This command allows options in authorizations without requiring that the TPM evaluate all of the options. If a policy may be satisfied by different sets of conditions, the TPM need only evaluate one set that satisfies the policy. This command will indicate that one of the required sets of conditions has been satisfied.
    */
    public TPM2_PolicyOR_REQUEST() {};
    /**
    * handle for the policy session being extended Auth Index: None
    */
    public TPM_HANDLE policySession;
    /**
    * number of digests in the list, minimum is two for TPM2_PolicyOR().
    */
    // private int pHashListCount;
    /**
    * the list of hashes to check for a match
    */
    public TPM2B_DIGEST[] pHashList;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        buf.writeInt((pHashList!=null)?pHashList.length:0, 4);
        if(pHashList!=null)
            buf.writeArrayOfTpmObjects(pHashList);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        int _pHashListCount = buf.readInt(4);
        pHashList = new TPM2B_DIGEST[_pHashListCount];
        for(int j=0;j<_pHashListCount;j++)pHashList[j]=new TPM2B_DIGEST();
        buf.readArrayOfTpmObjects(pHashList, _pHashListCount);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyOR_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyOR_REQUEST ret = new TPM2_PolicyOR_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyOR_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyOR_REQUEST ret = new TPM2_PolicyOR_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyOR_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "TPM2B_DIGEST", "pHashList", pHashList);
    };
    
    
};

//<<<

