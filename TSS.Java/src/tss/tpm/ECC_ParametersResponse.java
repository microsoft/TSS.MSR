package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.
*/
public class ECC_ParametersResponse extends TpmStructure
{
    /**
     * This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.
     * 
     * @param _parameters ECC parameters for the selected curve
     */
    public ECC_ParametersResponse(TPMS_ALGORITHM_DETAIL_ECC _parameters)
    {
        parameters = _parameters;
    }
    /**
    * This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.
    */
    public ECC_ParametersResponse() {};
    /**
    * ECC parameters for the selected curve
    */
    public TPMS_ALGORITHM_DETAIL_ECC parameters;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        parameters.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        parameters = TPMS_ALGORITHM_DETAIL_ECC.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static ECC_ParametersResponse fromTpm (byte[] x) 
    {
        ECC_ParametersResponse ret = new ECC_ParametersResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ECC_ParametersResponse fromTpm (InByteBuf buf) 
    {
        ECC_ParametersResponse ret = new ECC_ParametersResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECC_Parameters_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ALGORITHM_DETAIL_ECC", "parameters", parameters);
    };
    
    
};

//<<<

