package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This structure is used when the TPM returns a list of loaded handles when the capability in
 *  TPM2_GetCapability() is TPM_CAP_HANDLE.
 */
public class TPML_HANDLE extends TpmStructure implements TPMU_CAPABILITIES
{
    /** an array of handles */
    public TPM_HANDLE[] handle;
    
    public TPML_HANDLE() {}
    
    /** @param _handle an array of handles */
    public TPML_HANDLE(TPM_HANDLE[] _handle) { handle = _handle; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeObjArr(handle);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _count = buf.readInt();
        handle = new TPM_HANDLE[_count];
        for (int j=0; j < _count; j++) handle[j] = new TPM_HANDLE();
        buf.readArrayOfTpmObjects(handle, _count);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPML_HANDLE fromTpm (byte[] x) 
    {
        TPML_HANDLE ret = new TPML_HANDLE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPML_HANDLE fromTpm (InByteBuf buf) 
    {
        TPML_HANDLE ret = new TPML_HANDLE();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_HANDLE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
    }
}

//<<<

