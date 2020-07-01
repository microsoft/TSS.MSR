package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.  */
public class ECC_ParametersResponse extends RespStructure
{
    /** ECC parameters for the selected curve  */
    public TPMS_ALGORITHM_DETAIL_ECC parameters;
    
    public ECC_ParametersResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { parameters.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { parameters = TPMS_ALGORITHM_DETAIL_ECC.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static ECC_ParametersResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ECC_ParametersResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ECC_ParametersResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static ECC_ParametersResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ECC_ParametersResponse.class);
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
    }
}

//<<<
