package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the next bytesRequested octets from the random number generator (RNG).  */
public class GetRandomResponse extends TpmStructure
{
    /** The random octets  */
    public byte[] randomBytes;
    
    public GetRandomResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(randomBytes);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _randomBytesSize = buf.readShort() & 0xFFFF;
        randomBytes = new byte[_randomBytesSize];
        buf.readArrayOfInts(randomBytes, 1, _randomBytesSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static GetRandomResponse fromBytes (byte[] byteBuf) 
    {
        GetRandomResponse ret = new GetRandomResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static GetRandomResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static GetRandomResponse fromTpm (InByteBuf buf) 
    {
        GetRandomResponse ret = new GetRandomResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetRandom_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "randomBytes", randomBytes);
    }
}

//<<<
