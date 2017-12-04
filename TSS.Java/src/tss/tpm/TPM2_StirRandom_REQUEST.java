package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to add "additional information" to the RNG state.
*/
public class TPM2_StirRandom_REQUEST extends TpmStructure
{
    /**
     * This command is used to add "additional information" to the RNG state.
     * 
     * @param _inData additional information
     */
    public TPM2_StirRandom_REQUEST(byte[] _inData)
    {
        inData = _inData;
    }
    /**
    * This command is used to add "additional information" to the RNG state.
    */
    public TPM2_StirRandom_REQUEST() {};
    // private short inDataSize;
    /**
    * additional information
    */
    public byte[] inData;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((inData!=null)?inData.length:0, 2);
        if(inData!=null)
            buf.write(inData);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _inDataSize = buf.readInt(2);
        inData = new byte[_inDataSize];
        buf.readArrayOfInts(inData, 1, _inDataSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_StirRandom_REQUEST fromTpm (byte[] x) 
    {
        TPM2_StirRandom_REQUEST ret = new TPM2_StirRandom_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
    };
    
    
};

//<<<

