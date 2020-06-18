package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to add "additional information" to the RNG state.  */
public class TPM2_StirRandom_REQUEST extends TpmStructure
{
    /** Additional information  */
    public byte[] inData;
    
    public TPM2_StirRandom_REQUEST() {}
    
    /** @param _inData Additional information  */
    public TPM2_StirRandom_REQUEST(byte[] _inData) { inData = _inData; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(inData);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _inDataSize = buf.readShort() & 0xFFFF;
        inData = new byte[_inDataSize];
        buf.readArrayOfInts(inData, 1, _inDataSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_StirRandom_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_StirRandom_REQUEST ret = new TPM2_StirRandom_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_StirRandom_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_StirRandom_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_StirRandom_REQUEST ret = new TPM2_StirRandom_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_StirRandom_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "inData", inData);
    }
}

//<<<
