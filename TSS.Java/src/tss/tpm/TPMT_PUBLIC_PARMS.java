package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This structure is used in TPM2_TestParms() to validate that a set of algorithm
 *  parameters is supported by the TPM.
 */
public class TPMT_PUBLIC_PARMS extends TpmStructure
{
    public TPM_ALG_ID type() { return parameters.GetUnionSelector(); }
    
    /** the algorithm details */
    public TPMU_PUBLIC_PARMS parameters;
    
    public TPMT_PUBLIC_PARMS() {}
    
    /**
     *  @param _parameters the algorithm details
     *         (One of [TPMS_KEYEDHASH_PARMS, TPMS_SYMCIPHER_PARMS, TPMS_RSA_PARMS,
     *         TPMS_ECC_PARMS, TPMS_ASYM_PARMS])
     */
    public TPMT_PUBLIC_PARMS(TPMU_PUBLIC_PARMS _parameters) { parameters = _parameters; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        if (parameters == null) return;
        parameters.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)parameters).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _type = buf.readShort() & 0xFFFF;
        parameters = UnionFactory.create("TPMU_PUBLIC_PARMS", new TPM_ALG_ID(_type));
        parameters.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPMT_PUBLIC_PARMS fromTpm (byte[] x) 
    {
        TPMT_PUBLIC_PARMS ret = new TPMT_PUBLIC_PARMS();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMT_PUBLIC_PARMS fromTpm (InByteBuf buf) 
    {
        TPMT_PUBLIC_PARMS ret = new TPMT_PUBLIC_PARMS();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_PUBLIC_PARMS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_PUBLIC_PARMS", "parameters", parameters);
    }
}

//<<<
