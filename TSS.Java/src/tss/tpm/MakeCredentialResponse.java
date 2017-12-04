package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows the TPM to perform the actions required of a Certificate Authority (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
*/
public class MakeCredentialResponse extends TpmStructure
{
    /**
     * This command allows the TPM to perform the actions required of a Certificate Authority (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
     * 
     * @param _credentialBlob the credential 
     * @param _secret handle algorithm-dependent data that wraps the key that encrypts credentialBlob
     */
    public MakeCredentialResponse(TPMS_ID_OBJECT _credentialBlob,byte[] _secret)
    {
        credentialBlob = _credentialBlob;
        secret = _secret;
    }
    /**
    * This command allows the TPM to perform the actions required of a Certificate Authority (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
    */
    public MakeCredentialResponse() {};
    /**
    * size of the credential structure
    */
    // private short credentialBlobSize;
    /**
    * the credential
    */
    public TPMS_ID_OBJECT credentialBlob;
    /**
    * size of the secret value
    */
    // private short secretSize;
    /**
    * handle algorithm-dependent data that wraps the key that encrypts credentialBlob
    */
    public byte[] secret;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((credentialBlob!=null)?credentialBlob.toTpm().length:0, 2);
        if(credentialBlob!=null)
            credentialBlob.toTpm(buf);
        buf.writeInt((secret!=null)?secret.length:0, 2);
        if(secret!=null)
            buf.write(secret);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _credentialBlobSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _credentialBlobSize));
        credentialBlob = TPMS_ID_OBJECT.fromTpm(buf);
        buf.structSize.pop();
        int _secretSize = buf.readInt(2);
        secret = new byte[_secretSize];
        buf.readArrayOfInts(secret, 1, _secretSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static MakeCredentialResponse fromTpm (byte[] x) 
    {
        MakeCredentialResponse ret = new MakeCredentialResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static MakeCredentialResponse fromTpm (InByteBuf buf) 
    {
        MakeCredentialResponse ret = new MakeCredentialResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_MakeCredential_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ID_OBJECT", "credentialBlob", credentialBlob);
        _p.add(d, "byte", "secret", secret);
    };
    
    
};

//<<<

