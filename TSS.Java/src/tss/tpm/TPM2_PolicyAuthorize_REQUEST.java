package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows policies to change. If a policy were static, then it would be
 *  difficult to add users to a policy. This command lets a policy authority sign a new
 *  policy so that it may be used in an existing policy.
 */
public class TPM2_PolicyAuthorize_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /** Digest of the policy being approved  */
    public byte[] approvedPolicy;
    
    /** A policy qualifier  */
    public byte[] policyRef;
    
    /** Name of a key that can sign a policy addition  */
    public byte[] keySign;
    
    /** Ticket validating that approvedPolicy and policyRef were signed by keySign  */
    public TPMT_TK_VERIFIED checkTicket;
    
    public TPM2_PolicyAuthorize_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _approvedPolicy Digest of the policy being approved
     *  @param _policyRef A policy qualifier
     *  @param _keySign Name of a key that can sign a policy addition
     *  @param _checkTicket Ticket validating that approvedPolicy and policyRef were signed by
     *  keySign
     */
    public TPM2_PolicyAuthorize_REQUEST(TPM_HANDLE _policySession, byte[] _approvedPolicy, byte[] _policyRef, byte[] _keySign, TPMT_TK_VERIFIED _checkTicket)
    {
        policySession = _policySession;
        approvedPolicy = _approvedPolicy;
        policyRef = _policyRef;
        keySign = _keySign;
        checkTicket = _checkTicket;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(approvedPolicy);
        buf.writeSizedByteBuf(policyRef);
        buf.writeSizedByteBuf(keySign);
        checkTicket.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        approvedPolicy = buf.readSizedByteBuf();
        policyRef = buf.readSizedByteBuf();
        keySign = buf.readSizedByteBuf();
        checkTicket = TPMT_TK_VERIFIED.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyAuthorize_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyAuthorize_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyAuthorize_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyAuthorize_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyAuthorize_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyAuthorize_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte[]", "approvedPolicy", approvedPolicy);
        _p.add(d, "byte[]", "policyRef", policyRef);
        _p.add(d, "byte[]", "keySign", keySign);
        _p.add(d, "TPMT_TK_VERIFIED", "checkTicket", checkTicket);
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
