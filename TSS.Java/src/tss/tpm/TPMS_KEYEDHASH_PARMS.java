package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure describes the parameters that would appear in the public area of a
 *  KEYEDHASH object.
 */
public class TPMS_KEYEDHASH_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS
{
    /** Selects the scheme  */
    public TPM_ALG_ID schemeScheme() { return scheme != null ? scheme.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** Indicates the signing method used for a keyedHash signing object. This field also
     *  determines the size of the data field for a data object created with TPM2_Create() or
     *  TPM2_CreatePrimary().
     *  One of: TPMS_SCHEME_HMAC, TPMS_SCHEME_XOR, TPMS_NULL_SCHEME_KEYEDHASH.
     */
    public TPMU_SCHEME_KEYEDHASH scheme;

    public TPMS_KEYEDHASH_PARMS() {}

    /** @param _scheme Indicates the signing method used for a keyedHash signing object. This
     *         field also determines the size of the data field for a data object created with
     *         TPM2_Create() or TPM2_CreatePrimary().
     *         One of: TPMS_SCHEME_HMAC, TPMS_SCHEME_XOR, TPMS_NULL_SCHEME_KEYEDHASH.
     */
    public TPMS_KEYEDHASH_PARMS(TPMU_SCHEME_KEYEDHASH _scheme) { scheme = _scheme; }

    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.KEYEDHASH; }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        if (scheme == null) return;
        buf.writeShort(scheme.GetUnionSelector());
        scheme.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        TPM_ALG_ID schemeScheme = TPM_ALG_ID.fromTpm(buf);
        scheme = UnionFactory.create("TPMU_SCHEME_KEYEDHASH", schemeScheme);
        scheme.initFromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_KEYEDHASH_PARMS fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_KEYEDHASH_PARMS.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_KEYEDHASH_PARMS fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_KEYEDHASH_PARMS fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_KEYEDHASH_PARMS.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_KEYEDHASH_PARMS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_SCHEME_KEYEDHASH", "scheme", scheme);
    }
}

//<<<
