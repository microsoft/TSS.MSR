package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to set the desired PCR allocation of PCR and algorithms. This command requires Platform Authorization.
*/
public class TPM2_PCR_Allocate_REQUEST extends TpmStructure
{
    /**
     * This command is used to set the desired PCR allocation of PCR and algorithms. This command requires Platform Authorization.
     * 
     * @param _authHandle TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param _pcrAllocation the requested allocation
     */
    public TPM2_PCR_Allocate_REQUEST(TPM_HANDLE _authHandle,TPMS_PCR_SELECTION[] _pcrAllocation)
    {
        authHandle = _authHandle;
        pcrAllocation = _pcrAllocation;
    }
    /**
    * This command is used to set the desired PCR allocation of PCR and algorithms. This command requires Platform Authorization.
    */
    public TPM2_PCR_Allocate_REQUEST() {};
    /**
    * TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * number of selection structures A value of zero is allowed.
    */
    // private int pcrAllocationCount;
    /**
    * the requested allocation
    */
    public TPMS_PCR_SELECTION[] pcrAllocation;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
        buf.writeInt((pcrAllocation!=null)?pcrAllocation.length:0, 4);
        if(pcrAllocation!=null)
            buf.writeArrayOfTpmObjects(pcrAllocation);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
        int _pcrAllocationCount = buf.readInt(4);
        pcrAllocation = new TPMS_PCR_SELECTION[_pcrAllocationCount];
        for(int j=0;j<_pcrAllocationCount;j++)pcrAllocation[j]=new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(pcrAllocation, _pcrAllocationCount);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PCR_Allocate_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PCR_Allocate_REQUEST ret = new TPM2_PCR_Allocate_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PCR_Allocate_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PCR_Allocate_REQUEST ret = new TPM2_PCR_Allocate_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Allocate_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPMS_PCR_SELECTION", "pcrAllocation", pcrAllocation);
    };
    
    
};

//<<<

