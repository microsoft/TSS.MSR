package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. This is a deferred assertion. Values are stored in the policy session context and checked when the policy is used for authorization.
*/
public class TPM2_PolicyNvWritten_REQUEST extends TpmStructure
{
    /**
     * This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. This is a deferred assertion. Values are stored in the policy session context and checked when the policy is used for authorization.
     * 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _writtenSet YES if NV Index is required to have been written NO if NV Index is required not to have been written
     */
    public TPM2_PolicyNvWritten_REQUEST(TPM_HANDLE _policySession,byte _writtenSet)
    {
        policySession = _policySession;
        writtenSet = _writtenSet;
    }
    /**
    * This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. This is a deferred assertion. Values are stored in the policy session context and checked when the policy is used for authorization.
    */
    public TPM2_PolicyNvWritten_REQUEST() {};
    /**
    * handle for the policy session being extended Auth Index: None
    */
    public TPM_HANDLE policySession;
    /**
    * YES if NV Index is required to have been written NO if NV Index is required not to have been written
    */
    public byte writtenSet;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        buf.write(writtenSet);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        writtenSet = (byte) buf.readInt(1);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyNvWritten_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyNvWritten_REQUEST ret = new TPM2_PolicyNvWritten_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyNvWritten_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyNvWritten_REQUEST ret = new TPM2_PolicyNvWritten_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyNvWritten_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "BYTE", "writtenSet", writtenSet);
    };
    
    
};

//<<<

