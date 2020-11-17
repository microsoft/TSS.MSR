package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command enables the association of a credential with an object in a way that
 *  ensures that the TPM has validated the parameters of the credentialed object.
 */
public class TPM2_ActivateCredential_REQUEST extends ReqStructure
{
    /** Handle of the object associated with certificate in credentialBlob
     *  Auth Index: 1
     *  Auth Role: ADMIN
     */
    public TPM_HANDLE activateHandle;

    /** Loaded key used to decrypt the TPMS_SENSITIVE in credentialBlob
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE keyHandle;

    /** The credential */
    public TPMS_ID_OBJECT credentialBlob;

    /** KeyHandle algorithm-dependent encrypted seed that protects credentialBlob */
    public byte[] secret;

    public TPM2_ActivateCredential_REQUEST()
    {
        activateHandle = new TPM_HANDLE();
        keyHandle = new TPM_HANDLE();
    }

    /** @param _activateHandle Handle of the object associated with certificate in credentialBlob
     *         Auth Index: 1
     *         Auth Role: ADMIN
     *  @param _keyHandle Loaded key used to decrypt the TPMS_SENSITIVE in credentialBlob
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _credentialBlob The credential
     *  @param _secret KeyHandle algorithm-dependent encrypted seed that protects credentialBlob
     */
    public TPM2_ActivateCredential_REQUEST(TPM_HANDLE _activateHandle, TPM_HANDLE _keyHandle, TPMS_ID_OBJECT _credentialBlob, byte[] _secret)
    {
        activateHandle = _activateHandle;
        keyHandle = _keyHandle;
        credentialBlob = _credentialBlob;
        secret = _secret;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(credentialBlob);
        buf.writeSizedByteBuf(secret);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        credentialBlob = buf.createSizedObj(TPMS_ID_OBJECT.class);
        secret = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ActivateCredential_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ActivateCredential_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ActivateCredential_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ActivateCredential_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ActivateCredential_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ActivateCredential_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "activateHandle", activateHandle);
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "TPMS_ID_OBJECT", "credentialBlob", credentialBlob);
        _p.add(d, "byte[]", "secret", secret);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 2; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {activateHandle, keyHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
