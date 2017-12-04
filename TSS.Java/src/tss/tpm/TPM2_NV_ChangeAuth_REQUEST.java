package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows the authorization secret for an NV Index to be changed.
*/
public class TPM2_NV_ChangeAuth_REQUEST extends TpmStructure
{
    /**
     * This command allows the authorization secret for an NV Index to be changed.
     * 
     * @param _nvIndex handle of the entity Auth Index: 1 Auth Role: ADMIN 
     * @param _newAuth new authorization value
     */
    public TPM2_NV_ChangeAuth_REQUEST(TPM_HANDLE _nvIndex,byte[] _newAuth)
    {
        nvIndex = _nvIndex;
        newAuth = _newAuth;
    }
    /**
    * This command allows the authorization secret for an NV Index to be changed.
    */
    public TPM2_NV_ChangeAuth_REQUEST() {};
    /**
    * handle of the entity Auth Index: 1 Auth Role: ADMIN
    */
    public TPM_HANDLE nvIndex;
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
        nvIndex.toTpm(buf);
        buf.writeInt((newAuth!=null)?newAuth.length:0, 2);
        if(newAuth!=null)
            buf.write(newAuth);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        nvIndex = TPM_HANDLE.fromTpm(buf);
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
    public static TPM2_NV_ChangeAuth_REQUEST fromTpm (byte[] x) 
    {
        TPM2_NV_ChangeAuth_REQUEST ret = new TPM2_NV_ChangeAuth_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_NV_ChangeAuth_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_ChangeAuth_REQUEST ret = new TPM2_NV_ChangeAuth_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_ChangeAuth_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "byte", "newAuth", newAuth);
    };
    
    
};

//<<<

