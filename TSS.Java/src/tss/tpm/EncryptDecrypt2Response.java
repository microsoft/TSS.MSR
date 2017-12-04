package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is identical to TPM2_EncryptDecrypt(), except that the inData parameter is the first parameter. This permits inData to be parameter encrypted.
*/
public class EncryptDecrypt2Response extends TpmStructure
{
    /**
     * This command is identical to TPM2_EncryptDecrypt(), except that the inData parameter is the first parameter. This permits inData to be parameter encrypted.
     * 
     * @param _outData encrypted or decrypted output 
     * @param _ivOut chaining value to use for IV in next round
     */
    public EncryptDecrypt2Response(byte[] _outData,byte[] _ivOut)
    {
        outData = _outData;
        ivOut = _ivOut;
    }
    /**
    * This command is identical to TPM2_EncryptDecrypt(), except that the inData parameter is the first parameter. This permits inData to be parameter encrypted.
    */
    public EncryptDecrypt2Response() {};
    /**
    * size of the buffer
    */
    // private short outDataSize;
    /**
    * encrypted or decrypted output
    */
    public byte[] outData;
    /**
    * size of the IV value This value is fixed for a TPM implementation.
    */
    // private short ivOutSize;
    /**
    * chaining value to use for IV in next round
    */
    public byte[] ivOut;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((outData!=null)?outData.length:0, 2);
        if(outData!=null)
            buf.write(outData);
        buf.writeInt((ivOut!=null)?ivOut.length:0, 2);
        if(ivOut!=null)
            buf.write(ivOut);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outDataSize = buf.readInt(2);
        outData = new byte[_outDataSize];
        buf.readArrayOfInts(outData, 1, _outDataSize);
        int _ivOutSize = buf.readInt(2);
        ivOut = new byte[_ivOutSize];
        buf.readArrayOfInts(ivOut, 1, _ivOutSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static EncryptDecrypt2Response fromTpm (byte[] x) 
    {
        EncryptDecrypt2Response ret = new EncryptDecrypt2Response();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static EncryptDecrypt2Response fromTpm (InByteBuf buf) 
    {
        EncryptDecrypt2Response ret = new EncryptDecrypt2Response();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EncryptDecrypt2_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "outData", outData);
        _p.add(d, "byte", "ivOut", ivOut);
    };
    
    
};

//<<<

