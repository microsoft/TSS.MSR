package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
*/
public class TPM2_ActivateCredential_REQUEST extends TpmStructure
{
    /**
     * This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
     * 
     * @param _activateHandle handle of the object associated with certificate in credentialBlob Auth Index: 1 Auth Role: ADMIN 
     * @param _keyHandle loaded key used to decrypt the TPMS_SENSITIVE in credentialBlob Auth Index: 2 Auth Role: USER 
     * @param _credentialBlob the credential 
     * @param _secret keyHandle algorithm-dependent encrypted seed that protects credentialBlob
     */
    public TPM2_ActivateCredential_REQUEST(TPM_HANDLE _activateHandle,TPM_HANDLE _keyHandle,TPMS_ID_OBJECT _credentialBlob,byte[] _secret)
    {
        activateHandle = _activateHandle;
        keyHandle = _keyHandle;
        credentialBlob = _credentialBlob;
        secret = _secret;
    }
    /**
    * This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
    */
    public TPM2_ActivateCredential_REQUEST() {};
    /**
    * handle of the object associated with certificate in credentialBlob Auth Index: 1 Auth Role: ADMIN
    */
    public TPM_HANDLE activateHandle;
    /**
    * loaded key used to decrypt the TPMS_SENSITIVE in credentialBlob Auth Index: 2 Auth Role: USER
    */
    public TPM_HANDLE keyHandle;
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
    * keyHandle algorithm-dependent encrypted seed that protects credentialBlob
    */
    public byte[] secret;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        activateHandle.toTpm(buf);
        keyHandle.toTpm(buf);
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
        activateHandle = TPM_HANDLE.fromTpm(buf);
        keyHandle = TPM_HANDLE.fromTpm(buf);
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
    public static TPM2_ActivateCredential_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ActivateCredential_REQUEST ret = new TPM2_ActivateCredential_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ActivateCredential_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ActivateCredential_REQUEST ret = new TPM2_ActivateCredential_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
        _p.add(d, "byte", "secret", secret);
    };
    
    
};

//<<<

