package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns the current policyDigest of the session. This command allows the TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
*/
public class TPM2_PolicyGetDigest_REQUEST extends TpmStructure
{
    /**
     * This command returns the current policyDigest of the session. This command allows the TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
     * 
     * @param _policySession handle for the policy session Auth Index: None
     */
    public TPM2_PolicyGetDigest_REQUEST(TPM_HANDLE _policySession)
    {
        policySession = _policySession;
    }
    /**
    * This command returns the current policyDigest of the session. This command allows the TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
    */
    public TPM2_PolicyGetDigest_REQUEST() {};
    /**
    * handle for the policy session Auth Index: None
    */
    public TPM_HANDLE policySession;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyGetDigest_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyGetDigest_REQUEST ret = new TPM2_PolicyGetDigest_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyGetDigest_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyGetDigest_REQUEST ret = new TPM2_PolicyGetDigest_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyGetDigest_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
    };
    
    
};

//<<<

