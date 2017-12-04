package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows the platform to change the set of algorithms that are used by the TPM. The algorithmSet setting is a vendor-dependent value.
*/
public class TPM2_SetAlgorithmSet_REQUEST extends TpmStructure
{
    /**
     * This command allows the platform to change the set of algorithms that are used by the TPM. The algorithmSet setting is a vendor-dependent value.
     * 
     * @param _authHandle TPM_RH_PLATFORM Auth Index: 1 Auth Role: USER 
     * @param _algorithmSet a TPM vendor-dependent value indicating the algorithm set selection
     */
    public TPM2_SetAlgorithmSet_REQUEST(TPM_HANDLE _authHandle,int _algorithmSet)
    {
        authHandle = _authHandle;
        algorithmSet = _algorithmSet;
    }
    /**
    * This command allows the platform to change the set of algorithms that are used by the TPM. The algorithmSet setting is a vendor-dependent value.
    */
    public TPM2_SetAlgorithmSet_REQUEST() {};
    /**
    * TPM_RH_PLATFORM Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * a TPM vendor-dependent value indicating the algorithm set selection
    */
    public int algorithmSet;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
        buf.write(algorithmSet);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
        algorithmSet =  buf.readInt(4);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_SetAlgorithmSet_REQUEST fromTpm (byte[] x) 
    {
        TPM2_SetAlgorithmSet_REQUEST ret = new TPM2_SetAlgorithmSet_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_SetAlgorithmSet_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_SetAlgorithmSet_REQUEST ret = new TPM2_SetAlgorithmSet_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SetAlgorithmSet_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "uint", "algorithmSet", algorithmSet);
    };
    
    
};

//<<<

