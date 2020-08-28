package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command uses the TPM to recover the Z value from a public point (QB) and a
 *  private key (ds). It will perform the multiplication of the provided inPoint (QB) with
 *  the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ)
 *  [hds]QB; where h is the cofactor of the curve).
 */
public class TPM2_ECDH_ZGen_REQUEST extends ReqStructure
{
    /** Handle of a loaded ECC key
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE keyHandle;

    /** A public key  */
    public TPMS_ECC_POINT inPoint;

    public TPM2_ECDH_ZGen_REQUEST() { keyHandle = new TPM_HANDLE(); }

    /** @param _keyHandle Handle of a loaded ECC key
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _inPoint A public key
     */
    public TPM2_ECDH_ZGen_REQUEST(TPM_HANDLE _keyHandle, TPMS_ECC_POINT _inPoint)
    {
        keyHandle = _keyHandle;
        inPoint = _inPoint;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(inPoint); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { inPoint = buf.createSizedObj(TPMS_ECC_POINT.class); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_ECDH_ZGen_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ECDH_ZGen_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ECDH_ZGen_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_ECDH_ZGen_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ECDH_ZGen_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECDH_ZGen_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "TPMS_ECC_POINT", "inPoint", inPoint);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {keyHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
