package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows qualification of the sending (copying) of an Object to an Attached Component (AC). Qualification includes selection of the receiving AC and the method of authentication for the AC, and, in certain circumstances, the Object to be sent may be specified.
*/
public class Policy_AC_SendSelectResponse extends TpmStructure
{
    /**
     * This command allows qualification of the sending (copying) of an Object to an Attached Component (AC). Qualification includes selection of the receiving AC and the method of authentication for the AC, and, in certain circumstances, the Object to be sent may be specified.
     */
    public Policy_AC_SendSelectResponse()
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
    public static Policy_AC_SendSelectResponse fromTpm (byte[] x) 
    {
        Policy_AC_SendSelectResponse ret = new Policy_AC_SendSelectResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static Policy_AC_SendSelectResponse fromTpm (InByteBuf buf) 
    {
        Policy_AC_SendSelectResponse ret = new Policy_AC_SendSelectResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Policy_AC_SendSelect_RESPONSE");
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

