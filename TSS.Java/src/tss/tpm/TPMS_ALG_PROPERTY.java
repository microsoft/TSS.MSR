package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used to report the properties of an algorithm identifier. It is
 *  returned in response to a TPM2_GetCapability() with capability = TPM_CAP_ALG.
 */
public class TPMS_ALG_PROPERTY extends TpmStructure
{
    /** An algorithm identifier  */
    public TPM_ALG_ID alg;
    
    /** The attributes of the algorithm  */
    public TPMA_ALGORITHM algProperties;
    
    public TPMS_ALG_PROPERTY() { alg = TPM_ALG_ID.NULL; }
    
    /** @param _alg An algorithm identifier
     *  @param _algProperties The attributes of the algorithm
     */
    public TPMS_ALG_PROPERTY(TPM_ALG_ID _alg, TPMA_ALGORITHM _algProperties)
    {
        alg = _alg;
        algProperties = _algProperties;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        alg.toTpm(buf);
        algProperties.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        alg = TPM_ALG_ID.fromTpm(buf);
        algProperties = TPMA_ALGORITHM.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_ALG_PROPERTY fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_ALG_PROPERTY.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_ALG_PROPERTY fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_ALG_PROPERTY fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_ALG_PROPERTY.class);
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
    }
}

//<<<
