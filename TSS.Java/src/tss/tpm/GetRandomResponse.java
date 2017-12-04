package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns the next bytesRequested octets from the random number generator (RNG).
*/
public class GetRandomResponse extends TpmStructure
{
    /**
     * This command returns the next bytesRequested octets from the random number generator (RNG).
     * 
     * @param _randomBytes the random octets
     */
    public GetRandomResponse(byte[] _randomBytes)
    {
        randomBytes = _randomBytes;
    }
    /**
    * This command returns the next bytesRequested octets from the random number generator (RNG).
    */
    public GetRandomResponse() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short randomBytesSize;
    /**
    * the random octets
    */
    public byte[] randomBytes;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((randomBytes!=null)?randomBytes.length:0, 2);
        if(randomBytes!=null)
            buf.write(randomBytes);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _randomBytesSize = buf.readInt(2);
        randomBytes = new byte[_randomBytesSize];
        buf.readArrayOfInts(randomBytes, 1, _randomBytesSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static GetRandomResponse fromTpm (byte[] x) 
    {
        GetRandomResponse ret = new GetRandomResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
    };
    
    
};

//<<<

