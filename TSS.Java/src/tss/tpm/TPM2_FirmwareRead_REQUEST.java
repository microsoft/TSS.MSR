package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to read a copy of the current firmware installed in the TPM. */
public class TPM2_FirmwareRead_REQUEST extends TpmStructure
{
    /**
     *  the number of previous calls to this command in this sequence
     *  set to 0 on the first call
     */
    public int sequenceNumber;
    
    public TPM2_FirmwareRead_REQUEST() {}
    
    /**
     *  @param _sequenceNumber the number of previous calls to this command in this sequence
     *         set to 0 on the first call
     */
    public TPM2_FirmwareRead_REQUEST(int _sequenceNumber) { sequenceNumber = _sequenceNumber; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(sequenceNumber);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        sequenceNumber = buf.readInt();
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPM2_FirmwareRead_REQUEST fromTpm (byte[] x) 
    {
        TPM2_FirmwareRead_REQUEST ret = new TPM2_FirmwareRead_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_FirmwareRead_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_FirmwareRead_REQUEST ret = new TPM2_FirmwareRead_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FirmwareRead_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "int", "sequenceNumber", sequenceNumber);
    }
}

//<<<
