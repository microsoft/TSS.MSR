package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command changes the lockout parameters.
*/
public class DictionaryAttackParametersResponse extends TpmStructure
{
    /**
     * This command changes the lockout parameters.
     */
    public DictionaryAttackParametersResponse()
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
    public static DictionaryAttackParametersResponse fromTpm (byte[] x) 
    {
        DictionaryAttackParametersResponse ret = new DictionaryAttackParametersResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static DictionaryAttackParametersResponse fromTpm (InByteBuf buf) 
    {
        DictionaryAttackParametersResponse ret = new DictionaryAttackParametersResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_DictionaryAttackParameters_RESPONSE");
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

