package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used in TPM2_GetCapability() to return the ACT data. */
public class TPMS_ACT_DATA extends TpmStructure
{
    /** a permanent handle */
    public TPM_HANDLE handle;
    
    /** the current timeout of the ACT */
    public int timeout;
    
    /** the state of the ACT */
    public TPMA_ACT attributes;
    
    public TPMS_ACT_DATA() { handle = new TPM_HANDLE(); }
    
    /**
     *  @param _handle a permanent handle
     *  @param _timeout the current timeout of the ACT
     *  @param _attributes the state of the ACT
     */
    public TPMS_ACT_DATA(TPM_HANDLE _handle, int _timeout, TPMA_ACT _attributes)
    {
        handle = _handle;
        timeout = _timeout;
        attributes = _attributes;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        buf.writeInt(timeout);
        attributes.toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        timeout = buf.readInt();
        int _attributes = buf.readInt();
        attributes = TPMA_ACT.fromInt(_attributes);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPMS_ACT_DATA fromTpm (byte[] x) 
    {
        TPMS_ACT_DATA ret = new TPMS_ACT_DATA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_ACT_DATA fromTpm (InByteBuf buf) 
    {
        TPMS_ACT_DATA ret = new TPMS_ACT_DATA();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ACT_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "int", "timeout", timeout);
        _p.add(d, "TPMA_ACT", "attributes", attributes);
    }
}

//<<<
