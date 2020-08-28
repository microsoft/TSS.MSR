package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows the platform to change the set of algorithms that are used by the
 *  TPM. The algorithmSet setting is a vendor-dependent value.
 */
public class TPM2_SetAlgorithmSet_REQUEST extends ReqStructure
{
    /** TPM_RH_PLATFORM
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;

    /** A TPM vendor-dependent value indicating the algorithm set selection  */
    public int algorithmSet;

    public TPM2_SetAlgorithmSet_REQUEST() { authHandle = new TPM_HANDLE(); }

    /** @param _authHandle TPM_RH_PLATFORM
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _algorithmSet A TPM vendor-dependent value indicating the algorithm set selection
     */
    public TPM2_SetAlgorithmSet_REQUEST(TPM_HANDLE _authHandle, int _algorithmSet)
    {
        authHandle = _authHandle;
        algorithmSet = _algorithmSet;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeInt(algorithmSet); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { algorithmSet = buf.readInt(); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_SetAlgorithmSet_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_SetAlgorithmSet_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_SetAlgorithmSet_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_SetAlgorithmSet_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_SetAlgorithmSet_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SetAlgorithmSet_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "int", "algorithmSet", algorithmSet);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authHandle}; }
}

//<<<
