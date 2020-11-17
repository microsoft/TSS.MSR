package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows the TPM to perform the actions required of a Certificate Authority
 *  (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
 */
public class TPM2_MakeCredential_REQUEST extends ReqStructure
{
    /** Loaded public area, used to encrypt the sensitive area containing the credential key
     *  Auth Index: None
     */
    public TPM_HANDLE handle;

    /** The credential information */
    public byte[] credential;

    /** Name of the object to which the credential applies */
    public byte[] objectName;

    public TPM2_MakeCredential_REQUEST() { handle = new TPM_HANDLE(); }

    /** @param _handle Loaded public area, used to encrypt the sensitive area containing the
     *         credential key
     *         Auth Index: None
     *  @param _credential The credential information
     *  @param _objectName Name of the object to which the credential applies
     */
    public TPM2_MakeCredential_REQUEST(TPM_HANDLE _handle, byte[] _credential, byte[] _objectName)
    {
        handle = _handle;
        credential = _credential;
        objectName = _objectName;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(credential);
        buf.writeSizedByteBuf(objectName);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        credential = buf.readSizedByteBuf();
        objectName = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_MakeCredential_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_MakeCredential_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_MakeCredential_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_MakeCredential_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_MakeCredential_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_MakeCredential_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte[]", "credential", credential);
        _p.add(d, "byte[]", "objectName", objectName);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {handle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
