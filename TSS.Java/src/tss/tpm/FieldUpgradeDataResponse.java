package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command will take the actual field upgrade image to be installed on the TPM. The
 *  exact format of fuData is vendor-specific. This command is only possible following a
 *  successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized
 *  TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
 */
public class FieldUpgradeDataResponse extends RespStructure
{
    /** Tagged digest of the next block
     *  TPM_ALG_NULL if field update is complete
     */
    public TPMT_HA nextDigest;

    /** Tagged digest of the first block of the sequence */
    public TPMT_HA firstDigest;

    public FieldUpgradeDataResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        nextDigest.toTpm(buf);
        firstDigest.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        nextDigest = TPMT_HA.fromTpm(buf);
        firstDigest = TPMT_HA.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static FieldUpgradeDataResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(FieldUpgradeDataResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static FieldUpgradeDataResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static FieldUpgradeDataResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(FieldUpgradeDataResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("FieldUpgradeDataResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_HA", "nextDigest", nextDigest);
        _p.add(d, "TPMT_HA", "firstDigest", firstDigest);
    }
}

//<<<
