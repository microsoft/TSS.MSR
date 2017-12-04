package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to prepare the TPM for a power cycle. The shutdownType parameter indicates how the subsequent TPM2_Startup() will be processed.
*/
public class ShutdownResponse extends TpmStructure
{
    /**
     * This command is used to prepare the TPM for a power cycle. The shutdownType parameter indicates how the subsequent TPM2_Startup() will be processed.
     */
    public ShutdownResponse()
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
    public static ShutdownResponse fromTpm (byte[] x) 
    {
        ShutdownResponse ret = new ShutdownResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ShutdownResponse fromTpm (InByteBuf buf) 
    {
        ShutdownResponse ret = new ShutdownResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Shutdown_RESPONSE");
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

