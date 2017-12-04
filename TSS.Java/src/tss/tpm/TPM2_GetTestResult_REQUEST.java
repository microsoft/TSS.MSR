package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns manufacturer-specific information regarding the results of a self-test and an indication of the test status.
*/
public class TPM2_GetTestResult_REQUEST extends TpmStructure
{
    /**
     * This command returns manufacturer-specific information regarding the results of a self-test and an indication of the test status.
     */
    public TPM2_GetTestResult_REQUEST()
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
    public static TPM2_GetTestResult_REQUEST fromTpm (byte[] x) 
    {
        TPM2_GetTestResult_REQUEST ret = new TPM2_GetTestResult_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_GetTestResult_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_GetTestResult_REQUEST ret = new TPM2_GetTestResult_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetTestResult_REQUEST");
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

