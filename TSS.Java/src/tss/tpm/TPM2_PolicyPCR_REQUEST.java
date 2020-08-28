package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to cause conditional gating of a policy based on PCR. This
 *  command together with TPM2_PolicyOR() allows one group of authorizations to occur when
 *  PCR are in one state and a different set of authorizations when the PCR are in a
 *  different state.
 */
public class TPM2_PolicyPCR_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;

    /** Expected digest value of the selected PCR using the hash algorithm of the session; may
     *  be zero length
     */
    public byte[] pcrDigest;

    /** The PCR to include in the check digest  */
    public TPMS_PCR_SELECTION[] pcrs;

    public TPM2_PolicyPCR_REQUEST() { policySession = new TPM_HANDLE(); }

    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _pcrDigest Expected digest value of the selected PCR using the hash algorithm of
     *         the session; may be zero length
     *  @param _pcrs The PCR to include in the check digest
     */
    public TPM2_PolicyPCR_REQUEST(TPM_HANDLE _policySession, byte[] _pcrDigest, TPMS_PCR_SELECTION[] _pcrs)
    {
        policySession = _policySession;
        pcrDigest = _pcrDigest;
        pcrs = _pcrs;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(pcrDigest);
        buf.writeObjArr(pcrs);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        pcrDigest = buf.readSizedByteBuf();
        pcrs = buf.readObjArr(TPMS_PCR_SELECTION.class);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_PolicyPCR_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyPCR_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyPCR_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_PolicyPCR_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyPCR_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyPCR_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte[]", "pcrDigest", pcrDigest);
        _p.add(d, "TPMS_PCR_SELECTION[]", "pcrs", pcrs);
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
