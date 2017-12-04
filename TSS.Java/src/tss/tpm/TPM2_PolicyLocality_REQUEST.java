package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command indicates that the authorization will be limited to a specific locality.
*/
public class TPM2_PolicyLocality_REQUEST extends TpmStructure
{
    /**
     * This command indicates that the authorization will be limited to a specific locality.
     * 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _locality the allowed localities for the policy
     */
    public TPM2_PolicyLocality_REQUEST(TPM_HANDLE _policySession,TPMA_LOCALITY _locality)
    {
        policySession = _policySession;
        locality = _locality;
    }
    /**
    * This command indicates that the authorization will be limited to a specific locality.
    */
    public TPM2_PolicyLocality_REQUEST() {};
    /**
    * handle for the policy session being extended Auth Index: None
    */
    public TPM_HANDLE policySession;
    /**
    * the allowed localities for the policy
    */
    public TPMA_LOCALITY locality;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        locality.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        int _locality = buf.readInt(1);
        locality = TPMA_LOCALITY.fromInt(_locality);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyLocality_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyLocality_REQUEST ret = new TPM2_PolicyLocality_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyLocality_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyLocality_REQUEST ret = new TPM2_PolicyLocality_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyLocality_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "TPMA_LOCALITY", "locality", locality);
    };
    
    
};

//<<<

