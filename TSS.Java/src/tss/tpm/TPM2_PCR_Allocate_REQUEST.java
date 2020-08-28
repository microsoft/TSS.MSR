package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to set the desired PCR allocation of PCR and algorithms. This
 *  command requires Platform Authorization.
 */
public class TPM2_PCR_Allocate_REQUEST extends ReqStructure
{
    /** TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;

    /** The requested allocation  */
    public TPMS_PCR_SELECTION[] pcrAllocation;

    public TPM2_PCR_Allocate_REQUEST() { authHandle = new TPM_HANDLE(); }

    /** @param _authHandle TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _pcrAllocation The requested allocation
     */
    public TPM2_PCR_Allocate_REQUEST(TPM_HANDLE _authHandle, TPMS_PCR_SELECTION[] _pcrAllocation)
    {
        authHandle = _authHandle;
        pcrAllocation = _pcrAllocation;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(pcrAllocation); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { pcrAllocation = buf.readObjArr(TPMS_PCR_SELECTION.class); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_PCR_Allocate_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PCR_Allocate_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PCR_Allocate_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_PCR_Allocate_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PCR_Allocate_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Allocate_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPMS_PCR_SELECTION[]", "pcrAllocation", pcrAllocation);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(4, 3); }
}

//<<<
