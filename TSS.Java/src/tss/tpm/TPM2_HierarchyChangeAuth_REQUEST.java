package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows the authorization secret for a hierarchy or lockout to be changed using the current authorization value as the command authorization.
*/
public class TPM2_HierarchyChangeAuth_REQUEST extends TpmStructure
{
    /**
     * This command allows the authorization secret for a hierarchy or lockout to be changed using the current authorization value as the command authorization.
     * 
     * @param _authHandle TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param _newAuth new authorization value
     */
    public TPM2_HierarchyChangeAuth_REQUEST(TPM_HANDLE _authHandle,byte[] _newAuth)
    {
        authHandle = _authHandle;
        newAuth = _newAuth;
    }
    /**
    * This command allows the authorization secret for a hierarchy or lockout to be changed using the current authorization value as the command authorization.
    */
    public TPM2_HierarchyChangeAuth_REQUEST() {};
    /**
    * TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
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
        authHandle.toTpm(buf);
        buf.writeInt((newAuth!=null)?newAuth.length:0, 2);
        if(newAuth!=null)
            buf.write(newAuth);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
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
    public static TPM2_HierarchyChangeAuth_REQUEST fromTpm (byte[] x) 
    {
        TPM2_HierarchyChangeAuth_REQUEST ret = new TPM2_HierarchyChangeAuth_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_HierarchyChangeAuth_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_HierarchyChangeAuth_REQUEST ret = new TPM2_HierarchyChangeAuth_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HierarchyChangeAuth_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "byte", "newAuth", newAuth);
    };
    
    
};

//<<<

