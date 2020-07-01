package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used in TPM2_TestParms() to validate that a set of algorithm
 *  parameters is supported by the TPM.
 */
public class TPMT_PUBLIC_PARMS extends TpmStructure
{
    /** The algorithm to be tested  */
    public TPM_ALG_ID type() { return parameters.GetUnionSelector(); }
    
    /** The algorithm details  */
    public TPMU_PUBLIC_PARMS parameters;
    
    public TPMT_PUBLIC_PARMS() {}
    
    /** @param _parameters The algorithm details
     *         (One of [TPMS_KEYEDHASH_PARMS, TPMS_SYMCIPHER_PARMS, TPMS_RSA_PARMS,
     *         TPMS_ECC_PARMS, TPMS_ASYM_PARMS])
     */
    public TPMT_PUBLIC_PARMS(TPMU_PUBLIC_PARMS _parameters) { parameters = _parameters; }
    
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
        TPM_ALG_ID type = TPM_ALG_ID.fromTpm(buf);
        parameters = UnionFactory.create("TPMU_PUBLIC_PARMS", type);
        parameters.initFromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMT_PUBLIC_PARMS fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_PUBLIC_PARMS.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_PUBLIC_PARMS fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMT_PUBLIC_PARMS fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_PUBLIC_PARMS.class);
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
