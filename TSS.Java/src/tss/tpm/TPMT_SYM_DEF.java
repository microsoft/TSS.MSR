package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The TPMT_SYM_DEF structure is used to select an algorithm to be used for parameter
 *  encryption in those cases when different symmetric algorithms may be selected.
 */
public class TPMT_SYM_DEF extends TpmStructure
{
    /** Indicates a symmetric algorithm */
    public TPM_ALG_ID algorithm;

    /** A supported key size */
    public int keyBits;

    /** The mode for the key */
    public TPM_ALG_ID mode;

    public TPMT_SYM_DEF()
    {
        algorithm = TPM_ALG_ID.NULL;
        mode = TPM_ALG_ID.NULL;
    }

    /** @param _algorithm Indicates a symmetric algorithm
     *  @param _keyBits A supported key size
     *  @param _mode The mode for the key
     */
    public TPMT_SYM_DEF(TPM_ALG_ID _algorithm, int _keyBits, TPM_ALG_ID _mode)
    {
        algorithm = _algorithm;
        keyBits = _keyBits;
        mode = _mode;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        algorithm.toTpm(buf);
        if (algorithm == TPM_ALG_ID.NULL) return;
        buf.writeShort(keyBits);
        mode.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        algorithm = TPM_ALG_ID.fromTpm(buf);
        if (algorithm == TPM_ALG_ID.NULL) return;
        keyBits = buf.readShort();
        mode = TPM_ALG_ID.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMT_SYM_DEF fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_SYM_DEF.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMT_SYM_DEF fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMT_SYM_DEF fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_SYM_DEF.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SYM_DEF");
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
    public static TPMT_SYM_DEF nullObject() { return new TPMT_SYM_DEF(); }
}

//<<<
