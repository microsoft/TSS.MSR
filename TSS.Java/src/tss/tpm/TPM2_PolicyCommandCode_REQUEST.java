package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command indicates that the authorization will be limited to a specific command code.  */
public class TPM2_PolicyCommandCode_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;

    /** The allowed commandCode  */
    public TPM_CC code;

    public TPM2_PolicyCommandCode_REQUEST() { policySession = new TPM_HANDLE(); }

    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _code The allowed commandCode
     */
    public TPM2_PolicyCommandCode_REQUEST(TPM_HANDLE _policySession, TPM_CC _code)
    {
        policySession = _policySession;
        code = _code;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { code.toTpm(buf); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { code = TPM_CC.fromTpm(buf); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_PolicyCommandCode_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyCommandCode_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyCommandCode_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_PolicyCommandCode_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyCommandCode_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyCommandCode_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "TPM_CC", "code", code);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {policySession}; }
}

//<<<
