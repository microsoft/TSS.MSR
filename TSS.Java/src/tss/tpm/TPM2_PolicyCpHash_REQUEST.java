package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to allow a policy to be bound to a specific command and command parameters.  */
public class TPM2_PolicyCpHash_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;

    /** The cpHash added to the policy  */
    public byte[] cpHashA;

    public TPM2_PolicyCpHash_REQUEST() { policySession = new TPM_HANDLE(); }

    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _cpHashA The cpHash added to the policy
     */
    public TPM2_PolicyCpHash_REQUEST(TPM_HANDLE _policySession, byte[] _cpHashA)
    {
        policySession = _policySession;
        cpHashA = _cpHashA;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(cpHashA); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { cpHashA = buf.readSizedByteBuf(); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_PolicyCpHash_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyCpHash_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyCpHash_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_PolicyCpHash_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyCpHash_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyCpHash_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte[]", "cpHashA", cpHashA);
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
