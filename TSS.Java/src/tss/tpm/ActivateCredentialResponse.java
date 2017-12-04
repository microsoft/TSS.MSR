package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
*/
public class ActivateCredentialResponse extends TpmStructure
{
    /**
     * This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
     * 
     * @param _certInfo the decrypted certificate information the data should be no larger than the size of the digest of the nameAlg associated with keyHandle
     */
    public ActivateCredentialResponse(byte[] _certInfo)
    {
        certInfo = _certInfo;
    }
    /**
    * This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
    */
    public ActivateCredentialResponse() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short certInfoSize;
    /**
    * the decrypted certificate information the data should be no larger than the size of the digest of the nameAlg associated with keyHandle
    */
    public byte[] certInfo;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((certInfo!=null)?certInfo.length:0, 2);
        if(certInfo!=null)
            buf.write(certInfo);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _certInfoSize = buf.readInt(2);
        certInfo = new byte[_certInfoSize];
        buf.readArrayOfInts(certInfo, 1, _certInfoSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static ActivateCredentialResponse fromTpm (byte[] x) 
    {
        ActivateCredentialResponse ret = new ActivateCredentialResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ActivateCredentialResponse fromTpm (InByteBuf buf) 
    {
        ActivateCredentialResponse ret = new ActivateCredentialResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ActivateCredential_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "certInfo", certInfo);
    };
    
    
};

//<<<

