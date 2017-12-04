package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command removes all TPM context associated with a specific Owner.
*/
public class TPM2_Clear_REQUEST extends TpmStructure
{
    /**
     * This command removes all TPM context associated with a specific Owner.
     * 
     * @param _authHandle TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER
     */
    public TPM2_Clear_REQUEST(TPM_HANDLE _authHandle)
    {
        authHandle = _authHandle;
    }
    /**
    * This command removes all TPM context associated with a specific Owner.
    */
    public TPM2_Clear_REQUEST() {};
    /**
    * TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER
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
    public static TPM2_Clear_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Clear_REQUEST ret = new TPM2_Clear_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_Clear_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Clear_REQUEST ret = new TPM2_Clear_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Clear_REQUEST");
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

