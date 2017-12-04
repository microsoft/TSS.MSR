package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to prepare the TPM for a power cycle. The shutdownType parameter indicates how the subsequent TPM2_Startup() will be processed.
*/
public class TPM2_Shutdown_REQUEST extends TpmStructure
{
    /**
     * This command is used to prepare the TPM for a power cycle. The shutdownType parameter indicates how the subsequent TPM2_Startup() will be processed.
     * 
     * @param _shutdownType TPM_SU_CLEAR or TPM_SU_STATE
     */
    public TPM2_Shutdown_REQUEST(TPM_SU _shutdownType)
    {
        shutdownType = _shutdownType;
    }
    /**
    * This command is used to prepare the TPM for a power cycle. The shutdownType parameter indicates how the subsequent TPM2_Startup() will be processed.
    */
    public TPM2_Shutdown_REQUEST() {};
    /**
    * TPM_SU_CLEAR or TPM_SU_STATE
    */
    public TPM_SU shutdownType;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        shutdownType.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        shutdownType = TPM_SU.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_Shutdown_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Shutdown_REQUEST ret = new TPM2_Shutdown_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_Shutdown_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Shutdown_REQUEST ret = new TPM2_Shutdown_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Shutdown_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_SU", "shutdownType", shutdownType);
    };
    
    
};

//<<<

