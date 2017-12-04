package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* If the attribute of a PCR allows the PCR to be reset and proper authorization is provided, then this command may be used to set the PCR in all banks to zero. The attributes of the PCR may restrict the locality that can perform the reset operation.
*/
public class TPM2_PCR_Reset_REQUEST extends TpmStructure
{
    /**
     * If the attribute of a PCR allows the PCR to be reset and proper authorization is provided, then this command may be used to set the PCR in all banks to zero. The attributes of the PCR may restrict the locality that can perform the reset operation.
     * 
     * @param _pcrHandle the PCR to reset Auth Index: 1 Auth Role: USER
     */
    public TPM2_PCR_Reset_REQUEST(TPM_HANDLE _pcrHandle)
    {
        pcrHandle = _pcrHandle;
    }
    /**
    * If the attribute of a PCR allows the PCR to be reset and proper authorization is provided, then this command may be used to set the PCR in all banks to zero. The attributes of the PCR may restrict the locality that can perform the reset operation.
    */
    public TPM2_PCR_Reset_REQUEST() {};
    /**
    * the PCR to reset Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE pcrHandle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        pcrHandle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        pcrHandle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PCR_Reset_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PCR_Reset_REQUEST ret = new TPM2_PCR_Reset_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PCR_Reset_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PCR_Reset_REQUEST ret = new TPM2_PCR_Reset_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Reset_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "pcrHandle", pcrHandle);
    };
    
    
};

//<<<

