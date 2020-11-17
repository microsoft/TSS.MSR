package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is similar to TPM2_PolicySigned() except that it takes a ticket instead
 *  of a signed authorization. The ticket represents a validated authorization that had an
 *  expiration time associated with it.
 */
public class TPM2_PolicyTicket_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;

    /** Time when authorization will expire
     *  The contents are TPM specific. This shall be the value returned when ticket was produced.
     */
    public byte[] timeout;

    /** Digest of the command parameters to which this authorization is limited
     *  If it is not limited, the parameter will be the Empty Buffer.
     */
    public byte[] cpHashA;

    /** Reference to a qualifier for the policy may be the Empty Buffer */
    public byte[] policyRef;

    /** Name of the object that provided the authorization */
    public byte[] authName;

    /** An authorization ticket returned by the TPM in response to a TPM2_PolicySigned() or
     *  TPM2_PolicySecret()
     */
    public TPMT_TK_AUTH ticket;

    public TPM2_PolicyTicket_REQUEST() { policySession = new TPM_HANDLE(); }

    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _timeout Time when authorization will expire
     *         The contents are TPM specific. This shall be the value returned when ticket was
     *  produced.
     *  @param _cpHashA Digest of the command parameters to which this authorization is limited
     *         If it is not limited, the parameter will be the Empty Buffer.
     *  @param _policyRef Reference to a qualifier for the policy may be the Empty Buffer
     *  @param _authName Name of the object that provided the authorization
     *  @param _ticket An authorization ticket returned by the TPM in response to a
     *         TPM2_PolicySigned() or TPM2_PolicySecret()
     */
    public TPM2_PolicyTicket_REQUEST(TPM_HANDLE _policySession, byte[] _timeout, byte[] _cpHashA, byte[] _policyRef, byte[] _authName, TPMT_TK_AUTH _ticket)
    {
        policySession = _policySession;
        timeout = _timeout;
        cpHashA = _cpHashA;
        policyRef = _policyRef;
        authName = _authName;
        ticket = _ticket;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(timeout);
        buf.writeSizedByteBuf(cpHashA);
        buf.writeSizedByteBuf(policyRef);
        buf.writeSizedByteBuf(authName);
        ticket.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        timeout = buf.readSizedByteBuf();
        cpHashA = buf.readSizedByteBuf();
        policyRef = buf.readSizedByteBuf();
        authName = buf.readSizedByteBuf();
        ticket = TPMT_TK_AUTH.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicyTicket_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyTicket_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicyTicket_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicyTicket_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyTicket_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyTicket_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte[]", "timeout", timeout);
        _p.add(d, "byte[]", "cpHashA", cpHashA);
        _p.add(d, "byte[]", "policyRef", policyRef);
        _p.add(d, "byte[]", "authName", authName);
        _p.add(d, "TPMT_TK_AUTH", "ticket", ticket);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {policySession}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
