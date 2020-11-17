package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the attested data for TPM2_GetSessionAuditDigest(). */
public class TPMS_SESSION_AUDIT_INFO extends TpmStructure implements TPMU_ATTEST
{
    /** Current exclusive status of the session
     *  TRUE if all of the commands recorded in the sessionDigest were executed without any
     *  intervening TPM command that did not use this audit session
     */
    public byte exclusiveSession;

    /** The current value of the session audit digest */
    public byte[] sessionDigest;

    public TPMS_SESSION_AUDIT_INFO() {}

    /** @param _exclusiveSession Current exclusive status of the session
     *         TRUE if all of the commands recorded in the sessionDigest were executed without
     *  any
     *         intervening TPM command that did not use this audit session
     *  @param _sessionDigest The current value of the session audit digest
     */
    public TPMS_SESSION_AUDIT_INFO(byte _exclusiveSession, byte[] _sessionDigest)
    {
        exclusiveSession = _exclusiveSession;
        sessionDigest = _sessionDigest;
    }

    /** TpmUnion method */
    public TPM_ST GetUnionSelector() { return TPM_ST.ATTEST_SESSION_AUDIT; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeByte(exclusiveSession);
        buf.writeSizedByteBuf(sessionDigest);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        exclusiveSession = buf.readByte();
        sessionDigest = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SESSION_AUDIT_INFO fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SESSION_AUDIT_INFO.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SESSION_AUDIT_INFO fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SESSION_AUDIT_INFO fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SESSION_AUDIT_INFO.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SESSION_AUDIT_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "exclusiveSession", exclusiveSession);
        _p.add(d, "byte[]", "sessionDigest", sessionDigest);
    }
}

//<<<
