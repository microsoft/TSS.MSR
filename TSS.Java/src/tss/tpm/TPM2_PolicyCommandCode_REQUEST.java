package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command indicates that the authorization will be limited to a specific command code.
*/
public class TPM2_PolicyCommandCode_REQUEST extends TpmStructure
{
    /**
     * This command indicates that the authorization will be limited to a specific command code.
     * 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _code the allowed commandCode
     */
    public TPM2_PolicyCommandCode_REQUEST(TPM_HANDLE _policySession,TPM_CC _code)
    {
        policySession = _policySession;
        code = _code;
    }
    /**
    * This command indicates that the authorization will be limited to a specific command code.
    */
    public TPM2_PolicyCommandCode_REQUEST() {};
    /**
    * handle for the policy session being extended Auth Index: None
    */
    public TPM_HANDLE policySession;
    /**
    * the allowed commandCode
    */
    public TPM_CC code;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        code.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        code = TPM_CC.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyCommandCode_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyCommandCode_REQUEST ret = new TPM2_PolicyCommandCode_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyCommandCode_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyCommandCode_REQUEST ret = new TPM2_PolicyCommandCode_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyCommandCode_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "TPM_CC", "code", code);
    };
    
    
};

//<<<

