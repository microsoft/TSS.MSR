package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This replaces the current platform primary seed (PPS) with a value from the RNG and sets platformPolicy to the default initialization value (the Empty Buffer).
*/
public class ChangePPSResponse extends TpmStructure
{
    /**
     * This replaces the current platform primary seed (PPS) with a value from the RNG and sets platformPolicy to the default initialization value (the Empty Buffer).
     */
    public ChangePPSResponse()
    {
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static ChangePPSResponse fromTpm (byte[] x) 
    {
        ChangePPSResponse ret = new ChangePPSResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ChangePPSResponse fromTpm (InByteBuf buf) 
    {
        ChangePPSResponse ret = new ChangePPSResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ChangePPS_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
    };
    
    
};

//<<<

