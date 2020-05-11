package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to read a copy of the current firmware installed in the TPM. */
public class FirmwareReadResponse extends TpmStructure
{
    /** field upgrade image data */
    public byte[] fuData;
    
    public FirmwareReadResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(fuData);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _fuDataSize = buf.readShort() & 0xFFFF;
        fuData = new byte[_fuDataSize];
        buf.readArrayOfInts(fuData, 1, _fuDataSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static FirmwareReadResponse fromTpm (byte[] x) 
    {
        FirmwareReadResponse ret = new FirmwareReadResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static FirmwareReadResponse fromTpm (InByteBuf buf) 
    {
        FirmwareReadResponse ret = new FirmwareReadResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FirmwareRead_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "fuData", fuData);
    }
}

//<<<
