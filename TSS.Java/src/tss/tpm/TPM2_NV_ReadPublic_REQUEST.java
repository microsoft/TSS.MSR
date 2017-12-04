package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to read the public area and Name of an NV Index. The public area of an Index is not privacy-sensitive and no authorization is required to read this data.
*/
public class TPM2_NV_ReadPublic_REQUEST extends TpmStructure
{
    /**
     * This command is used to read the public area and Name of an NV Index. The public area of an Index is not privacy-sensitive and no authorization is required to read this data.
     * 
     * @param _nvIndex the NV Index Auth Index: None
     */
    public TPM2_NV_ReadPublic_REQUEST(TPM_HANDLE _nvIndex)
    {
        nvIndex = _nvIndex;
    }
    /**
    * This command is used to read the public area and Name of an NV Index. The public area of an Index is not privacy-sensitive and no authorization is required to read this data.
    */
    public TPM2_NV_ReadPublic_REQUEST() {};
    /**
    * the NV Index Auth Index: None
    */
    public TPM_HANDLE nvIndex;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        nvIndex.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        nvIndex = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_NV_ReadPublic_REQUEST fromTpm (byte[] x) 
    {
        TPM2_NV_ReadPublic_REQUEST ret = new TPM2_NV_ReadPublic_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_NV_ReadPublic_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_ReadPublic_REQUEST ret = new TPM2_NV_ReadPublic_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_ReadPublic_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
    };
    
    
};

//<<<

