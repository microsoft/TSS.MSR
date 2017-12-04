package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to SET bits in an NV Index that was created as a bit field. Any number of bits from 0 to 64 may be SET. The contents of bits are ORed with the current contents of the NV Index.
*/
public class TPM2_NV_SetBits_REQUEST extends TpmStructure
{
    /**
     * This command is used to SET bits in an NV Index that was created as a bit field. Any number of bits from 0 to 64 may be SET. The contents of bits are ORed with the current contents of the NV Index.
     * 
     * @param _authHandle handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param _nvIndex NV Index of the area in which the bit is to be set Auth Index: None 
     * @param _bits the data to OR with the current contents
     */
    public TPM2_NV_SetBits_REQUEST(TPM_HANDLE _authHandle,TPM_HANDLE _nvIndex,long _bits)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        bits = _bits;
    }
    /**
    * This command is used to SET bits in an NV Index that was created as a bit field. Any number of bits from 0 to 64 may be SET. The contents of bits are ORed with the current contents of the NV Index.
    */
    public TPM2_NV_SetBits_REQUEST() {};
    /**
    * handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * NV Index of the area in which the bit is to be set Auth Index: None
    */
    public TPM_HANDLE nvIndex;
    /**
    * the data to OR with the current contents
    */
    public long bits;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
        nvIndex.toTpm(buf);
        buf.write(bits);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
        nvIndex = TPM_HANDLE.fromTpm(buf);
        bits = buf.readLong();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_NV_SetBits_REQUEST fromTpm (byte[] x) 
    {
        TPM2_NV_SetBits_REQUEST ret = new TPM2_NV_SetBits_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_NV_SetBits_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_SetBits_REQUEST ret = new TPM2_NV_SetBits_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_SetBits_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "ulong", "bits", bits);
    };
    
    
};

//<<<

