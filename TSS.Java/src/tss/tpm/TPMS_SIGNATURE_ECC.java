package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 187 Definition of {ECC} TPMS_SIGNATURE_ECC Structure */
public class TPMS_SIGNATURE_ECC extends TpmStructure implements TPMU_SIGNATURE
{
    /** The hash algorithm used in the signature process
     *  TPM_ALG_NULL is not allowed.
     */
    public TPM_ALG_ID hash;
    public byte[] signatureR;
    public byte[] signatureS;

    public TPMS_SIGNATURE_ECC() { hash = TPM_ALG_ID.NULL; }

    /** @param _hash The hash algorithm used in the signature process
     *         TPM_ALG_NULL is not allowed.
     *  @param _signatureR TBD
     *  @param _signatureS TBD
     */
    public TPMS_SIGNATURE_ECC(TPM_ALG_ID _hash, byte[] _signatureR, byte[] _signatureS)
    {
        hash = _hash;
        signatureR = _signatureR;
        signatureS = _signatureS;
    }

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ECDSA; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        hash.toTpm(buf);
        buf.writeSizedByteBuf(signatureR);
        buf.writeSizedByteBuf(signatureS);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        hash = TPM_ALG_ID.fromTpm(buf);
        signatureR = buf.readSizedByteBuf();
        signatureS = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SIGNATURE_ECC fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SIGNATURE_ECC.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SIGNATURE_ECC fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SIGNATURE_ECC fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SIGNATURE_ECC.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIGNATURE_ECC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hash", hash);
        _p.add(d, "byte[]", "signatureR", signatureR);
        _p.add(d, "byte[]", "signatureS", signatureS);
    }
}

//<<<
