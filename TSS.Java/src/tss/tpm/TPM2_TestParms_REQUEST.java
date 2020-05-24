package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command is used to check to see if specific combinations of algorithm
 *  parameters are supported.
 */
public class TPM2_TestParms_REQUEST extends TpmStructure
{
    /** the algorithm to be tested */
    public TPM_ALG_ID parametersType() { return parameters.GetUnionSelector(); }
    
    /** algorithm parameters to be validated */
    public TPMU_PUBLIC_PARMS parameters;
    
    public TPM2_TestParms_REQUEST() {}
    
    /**
     *  @param _parameters algorithm parameters to be validated
     *         (One of [TPMS_KEYEDHASH_PARMS, TPMS_SYMCIPHER_PARMS, TPMS_RSA_PARMS,
     *         TPMS_ECC_PARMS, TPMS_ASYM_PARMS])
     */
    public TPM2_TestParms_REQUEST(TPMU_PUBLIC_PARMS _parameters) { parameters = _parameters; }
    
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
        int _parametersType = buf.readShort() & 0xFFFF;
        parameters = UnionFactory.create("TPMU_PUBLIC_PARMS", new TPM_ALG_ID(_parametersType));
        parameters.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPM2_TestParms_REQUEST fromTpm (byte[] x) 
    {
        TPM2_TestParms_REQUEST ret = new TPM2_TestParms_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_TestParms_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_TestParms_REQUEST ret = new TPM2_TestParms_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_TestParms_REQUEST");
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
