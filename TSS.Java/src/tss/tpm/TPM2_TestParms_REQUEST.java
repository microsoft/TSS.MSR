package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to check to see if specific combinations of algorithm parameters
 *  are supported.
 */
public class TPM2_TestParms_REQUEST extends ReqStructure
{
    /** The algorithm to be tested  */
    public TPM_ALG_ID parametersType() { return parameters.GetUnionSelector(); }
    
    /** Algorithm parameters to be validated
     *  One of: TPMS_KEYEDHASH_PARMS, TPMS_SYMCIPHER_PARMS, TPMS_RSA_PARMS, TPMS_ECC_PARMS,
     *  TPMS_ASYM_PARMS.
     */
    public TPMU_PUBLIC_PARMS parameters;
    
    public TPM2_TestParms_REQUEST() {}
    
    /** @param _parameters Algorithm parameters to be validated
     *         One of: TPMS_KEYEDHASH_PARMS, TPMS_SYMCIPHER_PARMS, TPMS_RSA_PARMS, TPMS_ECC_PARMS,
     *         TPMS_ASYM_PARMS.
     */
    public TPM2_TestParms_REQUEST(TPMU_PUBLIC_PARMS _parameters) { parameters = _parameters; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        if (parameters == null) return;
        buf.writeShort(parameters.GetUnionSelector());
        parameters.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        TPM_ALG_ID parametersType = TPM_ALG_ID.fromTpm(buf);
        parameters = UnionFactory.create("TPMU_PUBLIC_PARMS", parametersType);
        parameters.initFromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_TestParms_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_TestParms_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_TestParms_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_TestParms_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_TestParms_REQUEST.class);
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
