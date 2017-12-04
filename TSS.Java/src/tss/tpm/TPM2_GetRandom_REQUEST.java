package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns the next bytesRequested octets from the random number generator (RNG).
*/
public class TPM2_GetRandom_REQUEST extends TpmStructure
{
    /**
     * This command returns the next bytesRequested octets from the random number generator (RNG).
     * 
     * @param _bytesRequested number of octets to return
     */
    public TPM2_GetRandom_REQUEST(int _bytesRequested)
    {
        bytesRequested = (short)_bytesRequested;
    }
    /**
    * This command returns the next bytesRequested octets from the random number generator (RNG).
    */
    public TPM2_GetRandom_REQUEST() {};
    /**
    * number of octets to return
    */
    public short bytesRequested;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.write(bytesRequested);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        bytesRequested = (short) buf.readInt(2);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_GetRandom_REQUEST fromTpm (byte[] x) 
    {
        TPM2_GetRandom_REQUEST ret = new TPM2_GetRandom_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_GetRandom_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_GetRandom_REQUEST ret = new TPM2_GetRandom_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetRandom_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "ushort", "bytesRequested", bytesRequested);
    };
    
    
};

//<<<

