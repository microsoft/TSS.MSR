package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the parameters of an ECC curve identified by its TCG-assigned curveID. */
public class TPM2_ECC_Parameters_REQUEST extends ReqStructure
{
    /** Parameter set selector */
    public TPM_ECC_CURVE curveID;

    public TPM2_ECC_Parameters_REQUEST() {}

    /** @param _curveID Parameter set selector */
    public TPM2_ECC_Parameters_REQUEST(TPM_ECC_CURVE _curveID) { curveID = _curveID; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { curveID.toTpm(buf); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { curveID = TPM_ECC_CURVE.fromTpm(buf); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ECC_Parameters_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ECC_Parameters_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ECC_Parameters_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ECC_Parameters_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ECC_Parameters_REQUEST.class);
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
    }
}

//<<<
