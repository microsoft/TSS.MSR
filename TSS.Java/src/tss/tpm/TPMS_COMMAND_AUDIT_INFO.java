package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the attested data for TPM2_GetCommandAuditDigest(). */
public class TPMS_COMMAND_AUDIT_INFO extends TpmStructure implements TPMU_ATTEST
{
    /** The monotonic audit counter */
    public long auditCounter;

    /** Hash algorithm used for the command audit */
    public TPM_ALG_ID digestAlg;

    /** The current value of the audit digest */
    public byte[] auditDigest;

    /** Digest of the command codes being audited using digestAlg */
    public byte[] commandDigest;

    public TPMS_COMMAND_AUDIT_INFO() { digestAlg = TPM_ALG_ID.NULL; }

    /** @param _auditCounter The monotonic audit counter
     *  @param _digestAlg Hash algorithm used for the command audit
     *  @param _auditDigest The current value of the audit digest
     *  @param _commandDigest Digest of the command codes being audited using digestAlg
     */
    public TPMS_COMMAND_AUDIT_INFO(long _auditCounter, TPM_ALG_ID _digestAlg, byte[] _auditDigest, byte[] _commandDigest)
    {
        auditCounter = _auditCounter;
        digestAlg = _digestAlg;
        auditDigest = _auditDigest;
        commandDigest = _commandDigest;
    }

    /** TpmUnion method */
    public TPM_ST GetUnionSelector() { return TPM_ST.ATTEST_COMMAND_AUDIT; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeInt64(auditCounter);
        digestAlg.toTpm(buf);
        buf.writeSizedByteBuf(auditDigest);
        buf.writeSizedByteBuf(commandDigest);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        auditCounter = buf.readInt64();
        digestAlg = TPM_ALG_ID.fromTpm(buf);
        auditDigest = buf.readSizedByteBuf();
        commandDigest = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_COMMAND_AUDIT_INFO fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_COMMAND_AUDIT_INFO.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_COMMAND_AUDIT_INFO fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_COMMAND_AUDIT_INFO fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_COMMAND_AUDIT_INFO.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_COMMAND_AUDIT_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "long", "auditCounter", auditCounter);
        _p.add(d, "TPM_ALG_ID", "digestAlg", digestAlg);
        _p.add(d, "byte[]", "auditDigest", auditDigest);
        _p.add(d, "byte[]", "commandDigest", commandDigest);
    }
}

//<<<
