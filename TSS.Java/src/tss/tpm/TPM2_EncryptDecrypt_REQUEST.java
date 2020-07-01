package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** NOTE 1 This command is deprecated, and TPM2_EncryptDecrypt2() is preferred. This
 *  should be reflected in platform-specific specifications.
 */
public class TPM2_EncryptDecrypt_REQUEST extends ReqStructure
{
    /** The symmetric key used for the operation
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE keyHandle;
    
    /** If YES, then the operation is decryption; if NO, the operation is encryption  */
    public byte decrypt;
    
    /** Symmetric encryption/decryption mode
     *  this field shall match the default mode of the key or be TPM_ALG_NULL.
     */
    public TPM_ALG_ID mode;
    
    /** An initial value as required by the algorithm  */
    public byte[] ivIn;
    
    /** The data to be encrypted/decrypted  */
    public byte[] inData;
    
    public TPM2_EncryptDecrypt_REQUEST()
    {
        keyHandle = new TPM_HANDLE();
        mode = TPM_ALG_ID.NULL;
    }
    
    /** @param _keyHandle The symmetric key used for the operation
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _decrypt If YES, then the operation is decryption; if NO, the operation is encryption
     *  @param _mode Symmetric encryption/decryption mode
     *         this field shall match the default mode of the key or be TPM_ALG_NULL.
     *  @param _ivIn An initial value as required by the algorithm
     *  @param _inData The data to be encrypted/decrypted
     */
    public TPM2_EncryptDecrypt_REQUEST(TPM_HANDLE _keyHandle, byte _decrypt, TPM_ALG_ID _mode, byte[] _ivIn, byte[] _inData)
    {
        keyHandle = _keyHandle;
        decrypt = _decrypt;
        mode = _mode;
        ivIn = _ivIn;
        inData = _inData;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeByte(decrypt);
        mode.toTpm(buf);
        buf.writeSizedByteBuf(ivIn);
        buf.writeSizedByteBuf(inData);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        decrypt = buf.readByte();
        mode = TPM_ALG_ID.fromTpm(buf);
        ivIn = buf.readSizedByteBuf();
        inData = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_EncryptDecrypt_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_EncryptDecrypt_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_EncryptDecrypt_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_EncryptDecrypt_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_EncryptDecrypt_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EncryptDecrypt_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte", "decrypt", decrypt);
        _p.add(d, "TPM_ALG_ID", "mode", mode);
        _p.add(d, "byte", "ivIn", ivIn);
        _p.add(d, "byte", "inData", inData);
    }

    @Override
    public int numHandles() { return 1; }
    
    public int numAuthHandles() { return 1; }
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {keyHandle}; }
}

//<<<
