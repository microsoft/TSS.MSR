package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This is a placeholder to allow testing of the dispatch code.
*/
public class Vendor_TCG_TestResponse extends TpmStructure
{
    /**
     * This is a placeholder to allow testing of the dispatch code.
     * 
     * @param _outputData dummy data
     */
    public Vendor_TCG_TestResponse(byte[] _outputData)
    {
        outputData = _outputData;
    }
    /**
    * This is a placeholder to allow testing of the dispatch code.
    */
    public Vendor_TCG_TestResponse() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short outputDataSize;
    /**
    * dummy data
    */
    public byte[] outputData;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((outputData!=null)?outputData.length:0, 2);
        if(outputData!=null)
            buf.write(outputData);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outputDataSize = buf.readInt(2);
        outputData = new byte[_outputDataSize];
        buf.readArrayOfInts(outputData, 1, _outputDataSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static Vendor_TCG_TestResponse fromTpm (byte[] x) 
    {
        Vendor_TCG_TestResponse ret = new Vendor_TCG_TestResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static Vendor_TCG_TestResponse fromTpm (InByteBuf buf) 
    {
        Vendor_TCG_TestResponse ret = new Vendor_TCG_TestResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Vendor_TCG_Test_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "outputData", outputData);
    };
    
    
};

//<<<

