package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to determine which commands require assertion of Physical Presence (PP) in addition to platformAuth/platformPolicy.
*/
public class PP_CommandsResponse extends TpmStructure
{
    /**
     * This command is used to determine which commands require assertion of Physical Presence (PP) in addition to platformAuth/platformPolicy.
     */
    public PP_CommandsResponse()
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
    public static PP_CommandsResponse fromTpm (byte[] x) 
    {
        PP_CommandsResponse ret = new PP_CommandsResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PP_CommandsResponse fromTpm (InByteBuf buf) 
    {
        PP_CommandsResponse ret = new PP_CommandsResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PP_Commands_RESPONSE");
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

