package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is a return value for a TPM2_GetCapability() that reads the installed algorithms.
*/
public class TPMS_ALGORITHM_DESCRIPTION extends TpmStructure
{
    /**
     * This structure is a return value for a TPM2_GetCapability() that reads the installed algorithms.
     * 
     * @param _alg an algorithm 
     * @param _attributes the attributes of the algorithm
     */
    public TPMS_ALGORITHM_DESCRIPTION(TPM_ALG_ID _alg,TPMA_ALGORITHM _attributes)
    {
        alg = _alg;
        attributes = _attributes;
    }
    /**
    * This structure is a return value for a TPM2_GetCapability() that reads the installed algorithms.
    */
    public TPMS_ALGORITHM_DESCRIPTION() {};
    /**
    * an algorithm
    */
    public TPM_ALG_ID alg;
    /**
    * the attributes of the algorithm
    */
    public TPMA_ALGORITHM attributes;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        alg.toTpm(buf);
        attributes.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        alg = TPM_ALG_ID.fromTpm(buf);
        int _attributes = buf.readInt(4);
        attributes = TPMA_ALGORITHM.fromInt(_attributes);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_ALGORITHM_DESCRIPTION fromTpm (byte[] x) 
    {
        TPMS_ALGORITHM_DESCRIPTION ret = new TPMS_ALGORITHM_DESCRIPTION();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_ALGORITHM_DESCRIPTION fromTpm (InByteBuf buf) 
    {
        TPMS_ALGORITHM_DESCRIPTION ret = new TPMS_ALGORITHM_DESCRIPTION();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ALGORITHM_DESCRIPTION");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "alg", alg);
        _p.add(d, "TPMA_ALGORITHM", "attributes", attributes);
    };
    
    
};

//<<<

