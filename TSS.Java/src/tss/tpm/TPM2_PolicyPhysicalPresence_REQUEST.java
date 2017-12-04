package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command indicates that physical presence will need to be asserted at the time the authorization is performed.
*/
public class TPM2_PolicyPhysicalPresence_REQUEST extends TpmStructure
{
    /**
     * This command indicates that physical presence will need to be asserted at the time the authorization is performed.
     * 
     * @param _policySession handle for the policy session being extended Auth Index: None
     */
    public TPM2_PolicyPhysicalPresence_REQUEST(TPM_HANDLE _policySession)
    {
        policySession = _policySession;
    }
    /**
    * This command indicates that physical presence will need to be asserted at the time the authorization is performed.
    */
    public TPM2_PolicyPhysicalPresence_REQUEST() {};
    /**
    * handle for the policy session being extended Auth Index: None
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
    public static TPM2_PolicyPhysicalPresence_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyPhysicalPresence_REQUEST ret = new TPM2_PolicyPhysicalPresence_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyPhysicalPresence_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyPhysicalPresence_REQUEST ret = new TPM2_PolicyPhysicalPresence_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyPhysicalPresence_REQUEST");
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

