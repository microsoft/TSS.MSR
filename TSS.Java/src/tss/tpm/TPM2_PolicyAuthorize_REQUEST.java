package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command allows policies to change. If a policy were static, then it would be
 *  difficult to add users to a policy. This command lets a policy authority sign a new policy
 *  so that it may be used in an existing policy.
 */
public class TPM2_PolicyAuthorize_REQUEST extends TpmStructure
{
    /**
     *  handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /** digest of the policy being approved */
    public byte[] approvedPolicy;
    
    /** a policy qualifier */
    public byte[] policyRef;
    
    /** Name of a key that can sign a policy addition */
    public byte[] keySign;
    
    /** ticket validating that approvedPolicy and policyRef were signed by keySign */
    public TPMT_TK_VERIFIED checkTicket;
    
    public TPM2_PolicyAuthorize_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /**
     *  @param _policySession handle for the policy session being extended
     *         Auth Index: None
     *  @param _approvedPolicy digest of the policy being approved
     *  @param _policyRef a policy qualifier
     *  @param _keySign Name of a key that can sign a policy addition
     *  @param _checkTicket ticket validating that approvedPolicy and policyRef were signed by keySign
     */
    public TPM2_PolicyAuthorize_REQUEST(TPM_HANDLE _policySession, byte[] _approvedPolicy, byte[] _policyRef, byte[] _keySign, TPMT_TK_VERIFIED _checkTicket)
    {
        policySession = _policySession;
        approvedPolicy = _approvedPolicy;
        policyRef = _policyRef;
        keySign = _keySign;
        checkTicket = _checkTicket;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        buf.writeSizedByteBuf(approvedPolicy);
        buf.writeSizedByteBuf(policyRef);
        buf.writeSizedByteBuf(keySign);
        checkTicket.toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        int _approvedPolicySize = buf.readShort() & 0xFFFF;
        approvedPolicy = new byte[_approvedPolicySize];
        buf.readArrayOfInts(approvedPolicy, 1, _approvedPolicySize);
        int _policyRefSize = buf.readShort() & 0xFFFF;
        policyRef = new byte[_policyRefSize];
        buf.readArrayOfInts(policyRef, 1, _policyRefSize);
        int _keySignSize = buf.readShort() & 0xFFFF;
        keySign = new byte[_keySignSize];
        buf.readArrayOfInts(keySign, 1, _keySignSize);
        checkTicket = TPMT_TK_VERIFIED.fromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_PolicyAuthorize_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyAuthorize_REQUEST ret = new TPM2_PolicyAuthorize_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_PolicyAuthorize_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyAuthorize_REQUEST ret = new TPM2_PolicyAuthorize_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
        _p.add(d, "byte", "approvedPolicy", approvedPolicy);
        _p.add(d, "byte", "policyRef", policyRef);
        _p.add(d, "byte", "keySign", keySign);
        _p.add(d, "TPMT_TK_VERIFIED", "checkTicket", checkTicket);
    }
}

//<<<
