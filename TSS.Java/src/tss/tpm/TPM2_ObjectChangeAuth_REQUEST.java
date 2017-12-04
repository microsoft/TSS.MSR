package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to change the authorization secret for a TPM-resident object.
*/
public class TPM2_ObjectChangeAuth_REQUEST extends TpmStructure
{
    /**
     * This command is used to change the authorization secret for a TPM-resident object.
     * 
     * @param _objectHandle handle of the object Auth Index: 1 Auth Role: ADMIN 
     * @param _parentHandle handle of the parent Auth Index: None 
     * @param _newAuth new authorization value
     */
    public TPM2_ObjectChangeAuth_REQUEST(TPM_HANDLE _objectHandle,TPM_HANDLE _parentHandle,byte[] _newAuth)
    {
        objectHandle = _objectHandle;
        parentHandle = _parentHandle;
        newAuth = _newAuth;
    }
    /**
    * This command is used to change the authorization secret for a TPM-resident object.
    */
    public TPM2_ObjectChangeAuth_REQUEST() {};
    /**
    * handle of the object Auth Index: 1 Auth Role: ADMIN
    */
    public TPM_HANDLE objectHandle;
    /**
    * handle of the parent Auth Index: None
    */
    public TPM_HANDLE parentHandle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short newAuthSize;
    /**
    * new authorization value
    */
    public byte[] newAuth;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        objectHandle.toTpm(buf);
        parentHandle.toTpm(buf);
        buf.writeInt((newAuth!=null)?newAuth.length:0, 2);
        if(newAuth!=null)
            buf.write(newAuth);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        objectHandle = TPM_HANDLE.fromTpm(buf);
        parentHandle = TPM_HANDLE.fromTpm(buf);
        int _newAuthSize = buf.readInt(2);
        newAuth = new byte[_newAuthSize];
        buf.readArrayOfInts(newAuth, 1, _newAuthSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ObjectChangeAuth_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ObjectChangeAuth_REQUEST ret = new TPM2_ObjectChangeAuth_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ObjectChangeAuth_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ObjectChangeAuth_REQUEST ret = new TPM2_ObjectChangeAuth_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ObjectChangeAuth_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "TPM_HANDLE", "parentHandle", parentHandle);
        _p.add(d, "byte", "newAuth", newAuth);
    };
    
    
};

//<<<

