package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows removal of a platform-created NV Index that has TPMA_NV_POLICY_DELETE SET.
*/
public class TPM2_NV_UndefineSpaceSpecial_REQUEST extends TpmStructure
{
    /**
     * This command allows removal of a platform-created NV Index that has TPMA_NV_POLICY_DELETE SET.
     * 
     * @param _nvIndex Index to be deleted Auth Index: 1 Auth Role: ADMIN 
     * @param _platform TPM_RH_PLATFORM + {PP} Auth Index: 2 Auth Role: USER
     */
    public TPM2_NV_UndefineSpaceSpecial_REQUEST(TPM_HANDLE _nvIndex,TPM_HANDLE _platform)
    {
        nvIndex = _nvIndex;
        platform = _platform;
    }
    /**
    * This command allows removal of a platform-created NV Index that has TPMA_NV_POLICY_DELETE SET.
    */
    public TPM2_NV_UndefineSpaceSpecial_REQUEST() {};
    /**
    * Index to be deleted Auth Index: 1 Auth Role: ADMIN
    */
    public TPM_HANDLE nvIndex;
    /**
    * TPM_RH_PLATFORM + {PP} Auth Index: 2 Auth Role: USER
    */
    public TPM_HANDLE platform;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        nvIndex.toTpm(buf);
        platform.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        nvIndex = TPM_HANDLE.fromTpm(buf);
        platform = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_NV_UndefineSpaceSpecial_REQUEST fromTpm (byte[] x) 
    {
        TPM2_NV_UndefineSpaceSpecial_REQUEST ret = new TPM2_NV_UndefineSpaceSpecial_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_NV_UndefineSpaceSpecial_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_UndefineSpaceSpecial_REQUEST ret = new TPM2_NV_UndefineSpaceSpecial_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_UndefineSpaceSpecial_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "TPM_HANDLE", "platform", platform);
    };
    
    
};

//<<<

