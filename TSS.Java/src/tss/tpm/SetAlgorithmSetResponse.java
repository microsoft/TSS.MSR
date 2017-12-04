package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows the platform to change the set of algorithms that are used by the TPM. The algorithmSet setting is a vendor-dependent value.
*/
public class SetAlgorithmSetResponse extends TpmStructure
{
    /**
     * This command allows the platform to change the set of algorithms that are used by the TPM. The algorithmSet setting is a vendor-dependent value.
     */
    public SetAlgorithmSetResponse()
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
    public static SetAlgorithmSetResponse fromTpm (byte[] x) 
    {
        SetAlgorithmSetResponse ret = new SetAlgorithmSetResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static SetAlgorithmSetResponse fromTpm (InByteBuf buf) 
    {
        SetAlgorithmSetResponse ret = new SetAlgorithmSetResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SetAlgorithmSet_RESPONSE");
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

