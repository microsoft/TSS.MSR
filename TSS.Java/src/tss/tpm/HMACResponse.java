package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command performs an HMAC on the supplied data using the indicated hash algorithm.
*/
public class HMACResponse extends TpmStructure
{
    /**
     * This command performs an HMAC on the supplied data using the indicated hash algorithm.
     * 
     * @param _outHMAC the returned HMAC in a sized buffer
     */
    public HMACResponse(byte[] _outHMAC)
    {
        outHMAC = _outHMAC;
    }
    /**
    * This command performs an HMAC on the supplied data using the indicated hash algorithm.
    */
    public HMACResponse() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short outHMACSize;
    /**
    * the returned HMAC in a sized buffer
    */
    public byte[] outHMAC;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((outHMAC!=null)?outHMAC.length:0, 2);
        if(outHMAC!=null)
            buf.write(outHMAC);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outHMACSize = buf.readInt(2);
        outHMAC = new byte[_outHMACSize];
        buf.readArrayOfInts(outHMAC, 1, _outHMACSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static HMACResponse fromTpm (byte[] x) 
    {
        HMACResponse ret = new HMACResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static HMACResponse fromTpm (InByteBuf buf) 
    {
        HMACResponse ret = new HMACResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HMAC_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "outHMAC", outHMAC);
    };
    
    
};

//<<<

