package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This replaces the current platform primary seed (PPS) with a value from the RNG and sets platformPolicy to the default initialization value (the Empty Buffer).
*/
public class TPM2_ChangePPS_REQUEST extends TpmStructure
{
    /**
     * This replaces the current platform primary seed (PPS) with a value from the RNG and sets platformPolicy to the default initialization value (the Empty Buffer).
     * 
     * @param _authHandle TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
     */
    public TPM2_ChangePPS_REQUEST(TPM_HANDLE _authHandle)
    {
        authHandle = _authHandle;
    }
    /**
    * This replaces the current platform primary seed (PPS) with a value from the RNG and sets platformPolicy to the default initialization value (the Empty Buffer).
    */
    public TPM2_ChangePPS_REQUEST() {};
    /**
    * TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ChangePPS_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ChangePPS_REQUEST ret = new TPM2_ChangePPS_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ChangePPS_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ChangePPS_REQUEST ret = new TPM2_ChangePPS_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ChangePPS_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
    };
    
    
};

//<<<

