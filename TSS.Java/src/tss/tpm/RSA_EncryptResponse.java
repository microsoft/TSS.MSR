package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command performs RSA encryption using the indicated padding scheme according to IETF RFC 8017. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
*/
public class RSA_EncryptResponse extends TpmStructure
{
    /**
     * This command performs RSA encryption using the indicated padding scheme according to IETF RFC 8017. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
     * 
     * @param _outData encrypted output
     */
    public RSA_EncryptResponse(byte[] _outData)
    {
        outData = _outData;
    }
    /**
    * This command performs RSA encryption using the indicated padding scheme according to IETF RFC 8017. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
    */
    public RSA_EncryptResponse() {};
    /**
    * size of the buffer The value of zero is only valid for create.
    */
    // private short outDataSize;
    /**
    * encrypted output
    */
    public byte[] outData;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((outData!=null)?outData.length:0, 2);
        if(outData!=null)
            buf.write(outData);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outDataSize = buf.readInt(2);
        outData = new byte[_outDataSize];
        buf.readArrayOfInts(outData, 1, _outDataSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static RSA_EncryptResponse fromTpm (byte[] x) 
    {
        RSA_EncryptResponse ret = new RSA_EncryptResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static RSA_EncryptResponse fromTpm (InByteBuf buf) 
    {
        RSA_EncryptResponse ret = new RSA_EncryptResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_RSA_Encrypt_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "outData", outData);
    };
    
    
};

//<<<

