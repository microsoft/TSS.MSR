package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is identical to TPM2_EncryptDecrypt(), except that the inData parameter is the first parameter. This permits inData to be parameter encrypted.
*/
public class TPM2_EncryptDecrypt2_REQUEST extends TpmStructure
{
    /**
     * This command is identical to TPM2_EncryptDecrypt(), except that the inData parameter is the first parameter. This permits inData to be parameter encrypted.
     * 
     * @param _keyHandle the symmetric key used for the operation Auth Index: 1 Auth Role: USER 
     * @param _inData the data to be encrypted/decrypted 
     * @param _decrypt if YES, then the operation is decryption; if NO, the operation is encryption 
     * @param _mode symmetric mode this field shall match the default mode of the key or be TPM_ALG_NULL. 
     * @param _ivIn an initial value as required by the algorithm
     */
    public TPM2_EncryptDecrypt2_REQUEST(TPM_HANDLE _keyHandle,byte[] _inData,byte _decrypt,TPM_ALG_ID _mode,byte[] _ivIn)
    {
        keyHandle = _keyHandle;
        inData = _inData;
        decrypt = _decrypt;
        mode = _mode;
        ivIn = _ivIn;
    }
    /**
    * This command is identical to TPM2_EncryptDecrypt(), except that the inData parameter is the first parameter. This permits inData to be parameter encrypted.
    */
    public TPM2_EncryptDecrypt2_REQUEST() {};
    /**
    * the symmetric key used for the operation Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE keyHandle;
    /**
    * size of the buffer
    */
    // private short inDataSize;
    /**
    * the data to be encrypted/decrypted
    */
    public byte[] inData;
    /**
    * if YES, then the operation is decryption; if NO, the operation is encryption
    */
    public byte decrypt;
    /**
    * symmetric mode this field shall match the default mode of the key or be TPM_ALG_NULL.
    */
    public TPM_ALG_ID mode;
    /**
    * size of the IV value This value is fixed for a TPM implementation.
    */
    // private short ivInSize;
    /**
    * an initial value as required by the algorithm
    */
    public byte[] ivIn;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        keyHandle.toTpm(buf);
        buf.writeInt((inData!=null)?inData.length:0, 2);
        if(inData!=null)
            buf.write(inData);
        buf.write(decrypt);
        mode.toTpm(buf);
        buf.writeInt((ivIn!=null)?ivIn.length:0, 2);
        if(ivIn!=null)
            buf.write(ivIn);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        keyHandle = TPM_HANDLE.fromTpm(buf);
        int _inDataSize = buf.readInt(2);
        inData = new byte[_inDataSize];
        buf.readArrayOfInts(inData, 1, _inDataSize);
        decrypt = (byte) buf.readInt(1);
        mode = TPM_ALG_ID.fromTpm(buf);
        int _ivInSize = buf.readInt(2);
        ivIn = new byte[_ivInSize];
        buf.readArrayOfInts(ivIn, 1, _ivInSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_EncryptDecrypt2_REQUEST fromTpm (byte[] x) 
    {
        TPM2_EncryptDecrypt2_REQUEST ret = new TPM2_EncryptDecrypt2_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
        _p.add(d, "BYTE", "decrypt", decrypt);
        _p.add(d, "TPM_ALG_ID", "mode", mode);
        _p.add(d, "byte", "ivIn", ivIn);
    };
    
    
};

//<<<

