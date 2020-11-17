package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is a return value for a TPM2_GetCapability() that reads the installed algorithms. */
public class TPMS_ALGORITHM_DESCRIPTION extends TpmStructure
{
    /** An algorithm */
    public TPM_ALG_ID alg;

    /** The attributes of the algorithm */
    public TPMA_ALGORITHM attributes;

    public TPMS_ALGORITHM_DESCRIPTION() { alg = TPM_ALG_ID.NULL; }

    /** @param _alg An algorithm
     *  @param _attributes The attributes of the algorithm
     */
    public TPMS_ALGORITHM_DESCRIPTION(TPM_ALG_ID _alg, TPMA_ALGORITHM _attributes)
    {
        alg = _alg;
        attributes = _attributes;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        alg.toTpm(buf);
        attributes.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        alg = TPM_ALG_ID.fromTpm(buf);
        attributes = TPMA_ALGORITHM.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_ALGORITHM_DESCRIPTION fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_ALGORITHM_DESCRIPTION.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_ALGORITHM_DESCRIPTION fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_ALGORITHM_DESCRIPTION fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_ALGORITHM_DESCRIPTION.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ALGORITHM_DESCRIPTION");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "alg", alg);
        _p.add(d, "TPMA_ALGORITHM", "attributes", attributes);
    }
}

//<<<
