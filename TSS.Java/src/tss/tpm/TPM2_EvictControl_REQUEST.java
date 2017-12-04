package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows certain Transient Objects to be made persistent or a persistent object to be evicted.
*/
public class TPM2_EvictControl_REQUEST extends TpmStructure
{
    /**
     * This command allows certain Transient Objects to be made persistent or a persistent object to be evicted.
     * 
     * @param _auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER 
     * @param _objectHandle the handle of a loaded object Auth Index: None 
     * @param _persistentHandle if objectHandle is a transient object handle, then this is the persistent handle for the object if objectHandle is a persistent object handle, then it shall be the same value as persistentHandle
     */
    public TPM2_EvictControl_REQUEST(TPM_HANDLE _auth,TPM_HANDLE _objectHandle,TPM_HANDLE _persistentHandle)
    {
        auth = _auth;
        objectHandle = _objectHandle;
        persistentHandle = _persistentHandle;
    }
    /**
    * This command allows certain Transient Objects to be made persistent or a persistent object to be evicted.
    */
    public TPM2_EvictControl_REQUEST() {};
    /**
    * TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER
    */
    public TPM_HANDLE auth;
    /**
    * the handle of a loaded object Auth Index: None
    */
    public TPM_HANDLE objectHandle;
    /**
    * if objectHandle is a transient object handle, then this is the persistent handle for the object if objectHandle is a persistent object handle, then it shall be the same value as persistentHandle
    */
    public TPM_HANDLE persistentHandle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        auth.toTpm(buf);
        objectHandle.toTpm(buf);
        persistentHandle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        auth = TPM_HANDLE.fromTpm(buf);
        objectHandle = TPM_HANDLE.fromTpm(buf);
        persistentHandle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_EvictControl_REQUEST fromTpm (byte[] x) 
    {
        TPM2_EvictControl_REQUEST ret = new TPM2_EvictControl_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_EvictControl_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_EvictControl_REQUEST ret = new TPM2_EvictControl_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EvictControl_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "TPM_HANDLE", "persistentHandle", persistentHandle);
    };
    
    
};

//<<<

