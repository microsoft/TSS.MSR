package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to add data to a hash or HMAC sequence. The amount of data in buffer may be any size up to the limits of the TPM.
*/
public class SequenceUpdateResponse extends TpmStructure
{
    /**
     * This command is used to add data to a hash or HMAC sequence. The amount of data in buffer may be any size up to the limits of the TPM.
     */
    public SequenceUpdateResponse()
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
    public static SequenceUpdateResponse fromTpm (byte[] x) 
    {
        SequenceUpdateResponse ret = new SequenceUpdateResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static SequenceUpdateResponse fromTpm (InByteBuf buf) 
    {
        SequenceUpdateResponse ret = new SequenceUpdateResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SequenceUpdate_RESPONSE");
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

