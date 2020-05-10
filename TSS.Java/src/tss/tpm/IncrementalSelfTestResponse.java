package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command causes the TPM to perform a test of the selected algorithms. */
public class IncrementalSelfTestResponse extends TpmStructure
{
    /** list of algorithms that need testing */
    public TPM_ALG_ID[] toDoList;
    
    public IncrementalSelfTestResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeObjArr(toDoList);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _toDoListCount = buf.readInt();
        toDoList = new TPM_ALG_ID[_toDoListCount];
        for (int j=0; j < _toDoListCount; j++) toDoList[j] = TPM_ALG_ID.fromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static IncrementalSelfTestResponse fromTpm (byte[] x) 
    {
        IncrementalSelfTestResponse ret = new IncrementalSelfTestResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static IncrementalSelfTestResponse fromTpm (InByteBuf buf) 
    {
        IncrementalSelfTestResponse ret = new IncrementalSelfTestResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_IncrementalSelfTest_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "toDoList", toDoList);
    }
}

//<<<

