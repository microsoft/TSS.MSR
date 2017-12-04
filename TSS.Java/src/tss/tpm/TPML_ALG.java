package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This list is returned by TPM2_IncrementalSelfTest().
*/
public class TPML_ALG extends TpmStructure
{
    /**
     * This list is returned by TPM2_IncrementalSelfTest().
     * 
     * @param _algorithms a list of algorithm IDs The maximum only applies to an algorithm list in a command. The response size is limited only by the size of the parameter buffer.
     */
    public TPML_ALG(TPM_ALG_ID[] _algorithms)
    {
        algorithms = _algorithms;
    }
    /**
    * This list is returned by TPM2_IncrementalSelfTest().
    */
    public TPML_ALG() {};
    /**
    * number of algorithms in the algorithms list; may be 0
    */
    // private int count;
    /**
    * a list of algorithm IDs The maximum only applies to an algorithm list in a command. The response size is limited only by the size of the parameter buffer.
    */
    public TPM_ALG_ID[] algorithms;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((algorithms!=null)?algorithms.length:0, 4);
        if(algorithms!=null)
            buf.writeArrayOfTpmObjects(algorithms);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _count = buf.readInt(4);
        algorithms = new TPM_ALG_ID[_count];
        for(int j=0;j<_count;j++){algorithms[j]=TPM_ALG_ID.fromTpm(buf);};
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPML_ALG fromTpm (byte[] x) 
    {
        TPML_ALG ret = new TPML_ALG();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPML_ALG fromTpm (InByteBuf buf) 
    {
        TPML_ALG ret = new TPML_ALG();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_ALG");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "algorithms", algorithms);
    };
    
    
};

//<<<

