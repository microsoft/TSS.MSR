package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used to report the properties of an algorithm identifier. It is returned in response to a TPM2_GetCapability() with capability = TPM_CAP_ALG.
*/
public class TPMS_ALG_PROPERTY extends TpmStructure
{
    /**
     * This structure is used to report the properties of an algorithm identifier. It is returned in response to a TPM2_GetCapability() with capability = TPM_CAP_ALG.
     * 
     * @param _alg an algorithm identifier 
     * @param _algProperties the attributes of the algorithm
     */
    public TPMS_ALG_PROPERTY(TPM_ALG_ID _alg,TPMA_ALGORITHM _algProperties)
    {
        alg = _alg;
        algProperties = _algProperties;
    }
    /**
    * This structure is used to report the properties of an algorithm identifier. It is returned in response to a TPM2_GetCapability() with capability = TPM_CAP_ALG.
    */
    public TPMS_ALG_PROPERTY() {};
    /**
    * an algorithm identifier
    */
    public TPM_ALG_ID alg;
    /**
    * the attributes of the algorithm
    */
    public TPMA_ALGORITHM algProperties;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        alg.toTpm(buf);
        algProperties.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        alg = TPM_ALG_ID.fromTpm(buf);
        int _algProperties = buf.readInt(4);
        algProperties = TPMA_ALGORITHM.fromInt(_algProperties);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_ALG_PROPERTY fromTpm (byte[] x) 
    {
        TPMS_ALG_PROPERTY ret = new TPMS_ALG_PROPERTY();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_ALG_PROPERTY fromTpm (InByteBuf buf) 
    {
        TPMS_ALG_PROPERTY ret = new TPMS_ALG_PROPERTY();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ALG_PROPERTY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "alg", alg);
        _p.add(d, "TPMA_ALGORITHM", "algProperties", algProperties);
    };
    
    
};

//<<<

