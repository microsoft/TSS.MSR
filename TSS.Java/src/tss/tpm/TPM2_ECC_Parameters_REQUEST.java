package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.
*/
public class TPM2_ECC_Parameters_REQUEST extends TpmStructure
{
    /**
     * This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.
     * 
     * @param _curveID parameter set selector
     */
    public TPM2_ECC_Parameters_REQUEST(TPM_ECC_CURVE _curveID)
    {
        curveID = _curveID;
    }
    /**
    * This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.
    */
    public TPM2_ECC_Parameters_REQUEST() {};
    /**
    * parameter set selector
    */
    public TPM_ECC_CURVE curveID;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        curveID.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        curveID = TPM_ECC_CURVE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ECC_Parameters_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ECC_Parameters_REQUEST ret = new TPM2_ECC_Parameters_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ECC_Parameters_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ECC_Parameters_REQUEST ret = new TPM2_ECC_Parameters_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECC_Parameters_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ECC_CURVE", "curveID", curveID);
    };
    
    
};

//<<<

