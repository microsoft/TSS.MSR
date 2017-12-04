package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command performs an HMAC or a block cipher MAC on the supplied data using the indicated algorithm.
*/
public class MACResponse extends TpmStructure
{
    /**
     * This command performs an HMAC or a block cipher MAC on the supplied data using the indicated algorithm.
     * 
     * @param _outMAC the returned MAC in a sized buffer
     */
    public MACResponse(byte[] _outMAC)
    {
        outMAC = _outMAC;
    }
    /**
    * This command performs an HMAC or a block cipher MAC on the supplied data using the indicated algorithm.
    */
    public MACResponse() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short outMACSize;
    /**
    * the returned MAC in a sized buffer
    */
    public byte[] outMAC;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((outMAC!=null)?outMAC.length:0, 2);
        if(outMAC!=null)
            buf.write(outMAC);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outMACSize = buf.readInt(2);
        outMAC = new byte[_outMACSize];
        buf.readArrayOfInts(outMAC, 1, _outMACSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
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
    };
    
    
};

//<<<

