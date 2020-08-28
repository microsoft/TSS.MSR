package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used when different symmetric block cipher (not XOR) algorithms may
 *  be selected. If the Object can be an ordinary parent (not a derivation parent), this
 *  must be the first field in the Object's parameter (see 12.2.3.7) field.
 */
public class TPMT_SYM_DEF_OBJECT extends TpmStructure
{
    /** Selects a symmetric block cipher
     *  When used in the parameter area of a parent object, this shall be a supported block
     *  cipher and not TPM_ALG_NULL
     */
    public TPM_ALG_ID algorithm;

    /** The key size  */
    public int keyBits;

    /** Default mode
     *  When used in the parameter area of a parent object, this shall be TPM_ALG_CFB.
     */
    public TPM_ALG_ID mode;

    public TPMT_SYM_DEF_OBJECT()
    {
        algorithm = TPM_ALG_ID.NULL;
        mode = TPM_ALG_ID.NULL;
    }

    /** @param _algorithm Selects a symmetric block cipher
     *         When used in the parameter area of a parent object, this shall be a supported block
     *         cipher and not TPM_ALG_NULL
     *  @param _keyBits The key size
     *  @param _mode Default mode
     *         When used in the parameter area of a parent object, this shall be TPM_ALG_CFB.
     */
    public TPMT_SYM_DEF_OBJECT(TPM_ALG_ID _algorithm, int _keyBits, TPM_ALG_ID _mode)
    {
        algorithm = _algorithm;
        keyBits = _keyBits;
        mode = _mode;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        algorithm.toTpm(buf);
        if (algorithm == TPM_ALG_ID.NULL) return;
        buf.writeShort(keyBits);
        mode.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        algorithm = TPM_ALG_ID.fromTpm(buf);
        if (algorithm == TPM_ALG_ID.NULL) return;
        keyBits = buf.readShort();
        mode = TPM_ALG_ID.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMT_SYM_DEF_OBJECT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_SYM_DEF_OBJECT.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_SYM_DEF_OBJECT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMT_SYM_DEF_OBJECT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_SYM_DEF_OBJECT.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SYM_DEF_OBJECT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "algorithm", algorithm);
        _p.add(d, "int", "keyBits", keyBits);
        _p.add(d, "TPM_ALG_ID", "mode", mode);
    }

    /** @deprecated Use default constructor instead */

    @Deprecated
    public static TPMT_SYM_DEF_OBJECT nullObject() { return new TPMT_SYM_DEF_OBJECT(); }
}

//<<<
