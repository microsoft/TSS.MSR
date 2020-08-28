package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** AuthValue shall not be larger than the size of the digest produced by the nameAlg of
 *  the object. seedValue shall be the size of the digest produced by the nameAlg of the object.
 */
public class TPMT_SENSITIVE extends TpmStructure
{
    /** Identifier for the sensitive area
     *  This shall be the same as the type parameter of the associated public area.
     */
    public TPM_ALG_ID sensitiveType() { return sensitive.GetUnionSelector(); }

    /** User authorization data
     *  The authValue may be a zero-length string.
     */
    public byte[] authValue;

    /** For a parent object, the optional protection seed; for other objects, the obfuscation value  */
    public byte[] seedValue;

    /** The type-specific private data
     *  One of: TPM2B_PRIVATE_KEY_RSA, TPM2B_ECC_PARAMETER, TPM2B_SENSITIVE_DATA,
     *  TPM2B_SYM_KEY, TPM2B_PRIVATE_VENDOR_SPECIFIC.
     */
    public TPMU_SENSITIVE_COMPOSITE sensitive;

    public TPMT_SENSITIVE() {}

    /** @param _authValue User authorization data
     *         The authValue may be a zero-length string.
     *  @param _seedValue For a parent object, the optional protection seed; for other objects,
     *         the obfuscation value
     *  @param _sensitive The type-specific private data
     *         One of: TPM2B_PRIVATE_KEY_RSA, TPM2B_ECC_PARAMETER, TPM2B_SENSITIVE_DATA,
     *         TPM2B_SYM_KEY, TPM2B_PRIVATE_VENDOR_SPECIFIC.
     */
    public TPMT_SENSITIVE(byte[] _authValue, byte[] _seedValue, TPMU_SENSITIVE_COMPOSITE _sensitive)
    {
        authValue = _authValue;
        seedValue = _seedValue;
        sensitive = _sensitive;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        if (sensitive == null) return;
        buf.writeShort(sensitive.GetUnionSelector());
        buf.writeSizedByteBuf(authValue);
        buf.writeSizedByteBuf(seedValue);
        sensitive.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        TPM_ALG_ID sensitiveType = TPM_ALG_ID.fromTpm(buf);
        authValue = buf.readSizedByteBuf();
        seedValue = buf.readSizedByteBuf();
        sensitive = UnionFactory.create("TPMU_SENSITIVE_COMPOSITE", sensitiveType);
        sensitive.initFromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMT_SENSITIVE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_SENSITIVE.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_SENSITIVE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMT_SENSITIVE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_SENSITIVE.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SENSITIVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "authValue", authValue);
        _p.add(d, "byte[]", "seedValue", seedValue);
        _p.add(d, "TPMU_SENSITIVE_COMPOSITE", "sensitive", sensitive);
    }
}

//<<<
