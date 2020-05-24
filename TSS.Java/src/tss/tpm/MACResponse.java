package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command performs an HMAC or a block cipher MAC on the supplied data
 *  using the indicated algorithm.
 */
public class MACResponse extends TpmStructure
{
    /** the returned MAC in a sized buffer */
    public byte[] outMAC;
    
    public MACResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(outMAC);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outMACSize = buf.readShort() & 0xFFFF;
        outMAC = new byte[_outMACSize];
        buf.readArrayOfInts(outMAC, 1, _outMACSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static MACResponse fromTpm (byte[] x) 
    {
        MACResponse ret = new MACResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static MACResponse fromTpm (InByteBuf buf) 
    {
        MACResponse ret = new MACResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_MAC_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "outMAC", outMAC);
    }
}

//<<<
