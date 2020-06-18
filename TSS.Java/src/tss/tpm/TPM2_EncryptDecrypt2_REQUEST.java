package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is identical to TPM2_EncryptDecrypt(), except that the inData parameter
 *  is the first parameter. This permits inData to be parameter encrypted.
 */
public class TPM2_EncryptDecrypt2_REQUEST extends TpmStructure
{
    /** The symmetric key used for the operation
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE keyHandle;
    
    /** The data to be encrypted/decrypted  */
    public byte[] inData;
    
    /** If YES, then the operation is decryption; if NO, the operation is encryption  */
    public byte decrypt;
    
    /** Symmetric mode
     *  this field shall match the default mode of the key or be TPM_ALG_NULL.
     */
    public TPM_ALG_ID mode;
    
    /** An initial value as required by the algorithm  */
    public byte[] ivIn;
    
    public TPM2_EncryptDecrypt2_REQUEST()
    {
        keyHandle = new TPM_HANDLE();
        mode = TPM_ALG_ID.NULL;
    }
    
    /** @param _keyHandle The symmetric key used for the operation
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _inData The data to be encrypted/decrypted
     *  @param _decrypt If YES, then the operation is decryption; if NO, the operation is encryption
     *  @param _mode Symmetric mode
     *         this field shall match the default mode of the key or be TPM_ALG_NULL.
     *  @param _ivIn An initial value as required by the algorithm
     */
    public TPM2_EncryptDecrypt2_REQUEST(TPM_HANDLE _keyHandle, byte[] _inData, byte _decrypt, TPM_ALG_ID _mode, byte[] _ivIn)
    {
        keyHandle = _keyHandle;
        inData = _inData;
        decrypt = _decrypt;
        mode = _mode;
        ivIn = _ivIn;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(inData);
        buf.writeByte(decrypt);
        mode.toTpm(buf);
        buf.writeSizedByteBuf(ivIn);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _inDataSize = buf.readShort() & 0xFFFF;
        inData = new byte[_inDataSize];
        buf.readArrayOfInts(inData, 1, _inDataSize);
        decrypt = buf.readByte();
        mode = TPM_ALG_ID.fromTpm(buf);
        int _ivInSize = buf.readShort() & 0xFFFF;
        ivIn = new byte[_ivInSize];
        buf.readArrayOfInts(ivIn, 1, _ivInSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_EncryptDecrypt2_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_EncryptDecrypt2_REQUEST ret = new TPM2_EncryptDecrypt2_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_EncryptDecrypt2_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_EncryptDecrypt2_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_EncryptDecrypt2_REQUEST ret = new TPM2_EncryptDecrypt2_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EncryptDecrypt2_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte", "inData", inData);
        _p.add(d, "byte", "decrypt", decrypt);
        _p.add(d, "TPM_ALG_ID", "mode", mode);
        _p.add(d, "byte", "ivIn", ivIn);
    }
}

//<<<
