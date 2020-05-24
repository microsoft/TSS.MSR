package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command changes the authValue of a PCR or group of PCR. */
public class TPM2_PCR_SetAuthValue_REQUEST extends TpmStructure
{
    /**
     *  handle for a PCR that may have an authorization value set
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE pcrHandle;
    
    /** the desired authorization value */
    public byte[] auth;
    
    public TPM2_PCR_SetAuthValue_REQUEST() { pcrHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _pcrHandle handle for a PCR that may have an authorization value set
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _auth the desired authorization value
     */
    public TPM2_PCR_SetAuthValue_REQUEST(TPM_HANDLE _pcrHandle, byte[] _auth)
    {
        pcrHandle = _pcrHandle;
        auth = _auth;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(auth);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _authSize = buf.readShort() & 0xFFFF;
        auth = new byte[_authSize];
        buf.readArrayOfInts(auth, 1, _authSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPM2_PCR_SetAuthValue_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PCR_SetAuthValue_REQUEST ret = new TPM2_PCR_SetAuthValue_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_PCR_SetAuthValue_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PCR_SetAuthValue_REQUEST ret = new TPM2_PCR_SetAuthValue_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_SetAuthValue_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "pcrHandle", pcrHandle);
        _p.add(d, "byte", "auth", auth);
    }
}

//<<<
