package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* TPM2_Startup() is always preceded by _TPM_Init, which is the physical indication that TPM initialization is necessary because of a system-wide reset. TPM2_Startup() is only valid after _TPM_Init. Additional TPM2_Startup() commands are not allowed after it has completed successfully. If a TPM requires TPM2_Startup() and another command is received, or if the TPM receives TPM2_Startup() when it is not required, the TPM shall return TPM_RC_INITIALIZE.
*/
public class TPM2_Startup_REQUEST extends TpmStructure
{
    /**
     * TPM2_Startup() is always preceded by _TPM_Init, which is the physical indication that TPM initialization is necessary because of a system-wide reset. TPM2_Startup() is only valid after _TPM_Init. Additional TPM2_Startup() commands are not allowed after it has completed successfully. If a TPM requires TPM2_Startup() and another command is received, or if the TPM receives TPM2_Startup() when it is not required, the TPM shall return TPM_RC_INITIALIZE.
     * 
     * @param _startupType TPM_SU_CLEAR or TPM_SU_STATE
     */
    public TPM2_Startup_REQUEST(TPM_SU _startupType)
    {
        startupType = _startupType;
    }
    /**
    * TPM2_Startup() is always preceded by _TPM_Init, which is the physical indication that TPM initialization is necessary because of a system-wide reset. TPM2_Startup() is only valid after _TPM_Init. Additional TPM2_Startup() commands are not allowed after it has completed successfully. If a TPM requires TPM2_Startup() and another command is received, or if the TPM receives TPM2_Startup() when it is not required, the TPM shall return TPM_RC_INITIALIZE.
    */
    public TPM2_Startup_REQUEST() {};
    /**
    * TPM_SU_CLEAR or TPM_SU_STATE
    */
    public TPM_SU startupType;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        startupType.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        startupType = TPM_SU.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_Startup_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Startup_REQUEST ret = new TPM2_Startup_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_Startup_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Startup_REQUEST ret = new TPM2_Startup_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Startup_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_SU", "startupType", startupType);
    };
    
    
};

//<<<

