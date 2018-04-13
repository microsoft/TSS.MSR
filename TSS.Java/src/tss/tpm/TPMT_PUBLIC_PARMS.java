package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used in TPM2_TestParms() to validate that a set of algorithm parameters is supported by the TPM.
*/
public class TPMT_PUBLIC_PARMS extends TpmStructure
{
    /**
     * This structure is used in TPM2_TestParms() to validate that a set of algorithm parameters is supported by the TPM.
     * 
     * @param _parameters the algorithm details (One of TPMS_KEYEDHASH_PARMS, TPMS_SYMCIPHER_PARMS, TPMS_RSA_PARMS, TPMS_ECC_PARMS, TPMS_ASYM_PARMS)
     */
    public TPMT_PUBLIC_PARMS(TPMU_PUBLIC_PARMS _parameters)
    {
        parameters = _parameters;
    }
    /**
    * This structure is used in TPM2_TestParms() to validate that a set of algorithm parameters is supported by the TPM.
    */
    public TPMT_PUBLIC_PARMS() {};
    /**
    * the algorithm to be tested
    */
    // private TPM_ALG_ID type;
    /**
    * the algorithm details
    */
    public TPMU_PUBLIC_PARMS parameters;
    public int GetUnionSelector_parameters()
    {
        if(parameters instanceof TPMS_KEYEDHASH_PARMS){return 0x0008; }
        if(parameters instanceof TPMS_SYMCIPHER_PARMS){return 0x0025; }
        if(parameters instanceof TPMS_RSA_PARMS){return 0x0001; }
        if(parameters instanceof TPMS_ECC_PARMS){return 0x0023; }
        if(parameters instanceof TPMS_ASYM_PARMS){return 0x7FFF; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(GetUnionSelector_parameters(), 2);
        ((TpmMarshaller)parameters).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _type = buf.readInt(2);
        parameters=null;
        if(_type==TPM_ALG_ID.KEYEDHASH.toInt()) {parameters = new TPMS_KEYEDHASH_PARMS();}
        else if(_type==TPM_ALG_ID.SYMCIPHER.toInt()) {parameters = new TPMS_SYMCIPHER_PARMS();}
        else if(_type==TPM_ALG_ID.RSA.toInt()) {parameters = new TPMS_RSA_PARMS();}
        else if(_type==TPM_ALG_ID.ECC.toInt()) {parameters = new TPMS_ECC_PARMS();}
        else if(_type==TPM_ALG_ID.ANY.toInt()) {parameters = new TPMS_ASYM_PARMS();}
        if(parameters==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_type).name());
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
    };
    
    
};

//<<<

