package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command is used to set the time remaining before an Authenticated
 *  Countdown Timer (ACT) expires.
 */
public class TPM2_ACT_SetTimeout_REQUEST extends TpmStructure
{
    /**
     *  Handle of the selected ACT
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE actHandle;
    
    /** the start timeout value for the ACT in seconds */
    public int startTimeout;
    
    public TPM2_ACT_SetTimeout_REQUEST() { actHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _actHandle Handle of the selected ACT
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _startTimeout the start timeout value for the ACT in seconds
     */
    public TPM2_ACT_SetTimeout_REQUEST(TPM_HANDLE _actHandle, int _startTimeout)
    {
        actHandle = _actHandle;
        startTimeout = _startTimeout;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        actHandle.toTpm(buf);
        buf.writeInt(startTimeout);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        actHandle = TPM_HANDLE.fromTpm(buf);
        startTimeout = buf.readInt();
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_ACT_SetTimeout_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ACT_SetTimeout_REQUEST ret = new TPM2_ACT_SetTimeout_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_ACT_SetTimeout_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ACT_SetTimeout_REQUEST ret = new TPM2_ACT_SetTimeout_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ACT_SetTimeout_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "actHandle", actHandle);
        _p.add(d, "int", "startTimeout", startTimeout);
    }
}

//<<<

