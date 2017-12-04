package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows the TPM to perform the actions required of a Certificate Authority (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
*/
public class TPM2_MakeCredential_REQUEST extends TpmStructure
{
    /**
     * This command allows the TPM to perform the actions required of a Certificate Authority (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
     * 
     * @param _handle loaded public area, used to encrypt the sensitive area containing the credential key Auth Index: None 
     * @param _credential the credential information 
     * @param _objectName Name of the object to which the credential applies
     */
    public TPM2_MakeCredential_REQUEST(TPM_HANDLE _handle,byte[] _credential,byte[] _objectName)
    {
        handle = _handle;
        credential = _credential;
        objectName = _objectName;
    }
    /**
    * This command allows the TPM to perform the actions required of a Certificate Authority (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
    */
    public TPM2_MakeCredential_REQUEST() {};
    /**
    * loaded public area, used to encrypt the sensitive area containing the credential key Auth Index: None
    */
    public TPM_HANDLE handle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short credentialSize;
    /**
    * the credential information
    */
    public byte[] credential;
    /**
    * size of the Name structure
    */
    // private short objectNameSize;
    /**
    * Name of the object to which the credential applies
    */
    public byte[] objectName;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        buf.writeInt((credential!=null)?credential.length:0, 2);
        if(credential!=null)
            buf.write(credential);
        buf.writeInt((objectName!=null)?objectName.length:0, 2);
        if(objectName!=null)
            buf.write(objectName);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        int _credentialSize = buf.readInt(2);
        credential = new byte[_credentialSize];
        buf.readArrayOfInts(credential, 1, _credentialSize);
        int _objectNameSize = buf.readInt(2);
        objectName = new byte[_objectNameSize];
        buf.readArrayOfInts(objectName, 1, _objectNameSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_MakeCredential_REQUEST fromTpm (byte[] x) 
    {
        TPM2_MakeCredential_REQUEST ret = new TPM2_MakeCredential_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_MakeCredential_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_MakeCredential_REQUEST ret = new TPM2_MakeCredential_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
        _p.add(d, "byte", "credential", credential);
        _p.add(d, "byte", "objectName", objectName);
    };
    
    
};

//<<<

