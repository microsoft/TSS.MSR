package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows a policy authorization session to be returned to its initial state. This command is used after the TPM returns TPM_RC_PCR_CHANGED. That response code indicates that a policy will fail because the PCR have changed after TPM2_PolicyPCR() was executed. Restarting the session allows the authorizations to be replayed because the session restarts with the same nonceTPM. If the PCR are valid for the policy, the policy may then succeed.
*/
public class TPM2_PolicyRestart_REQUEST extends TpmStructure
{
    /**
     * This command allows a policy authorization session to be returned to its initial state. This command is used after the TPM returns TPM_RC_PCR_CHANGED. That response code indicates that a policy will fail because the PCR have changed after TPM2_PolicyPCR() was executed. Restarting the session allows the authorizations to be replayed because the session restarts with the same nonceTPM. If the PCR are valid for the policy, the policy may then succeed.
     * 
     * @param _sessionHandle the handle for the policy session
     */
    public TPM2_PolicyRestart_REQUEST(TPM_HANDLE _sessionHandle)
    {
        sessionHandle = _sessionHandle;
    }
    /**
    * This command allows a policy authorization session to be returned to its initial state. This command is used after the TPM returns TPM_RC_PCR_CHANGED. That response code indicates that a policy will fail because the PCR have changed after TPM2_PolicyPCR() was executed. Restarting the session allows the authorizations to be replayed because the session restarts with the same nonceTPM. If the PCR are valid for the policy, the policy may then succeed.
    */
    public TPM2_PolicyRestart_REQUEST() {};
    /**
    * the handle for the policy session
    */
    public TPM_HANDLE sessionHandle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        sessionHandle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        sessionHandle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyRestart_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyRestart_REQUEST ret = new TPM2_PolicyRestart_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyRestart_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyRestart_REQUEST ret = new TPM2_PolicyRestart_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyRestart_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "sessionHandle", sessionHandle);
    };
    
    
};

//<<<

