package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to set the desired PCR allocation of PCR and algorithms. This
 *  command requires Platform Authorization.
 */
public class TPM2_PCR_Allocate_REQUEST extends TpmStructure
{
    /** TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** The requested allocation  */
    public TPMS_PCR_SELECTION[] pcrAllocation;
    
    public TPM2_PCR_Allocate_REQUEST() { authHandle = new TPM_HANDLE(); }
    
    /** @param _authHandle TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _pcrAllocation The requested allocation
     */
    public TPM2_PCR_Allocate_REQUEST(TPM_HANDLE _authHandle, TPMS_PCR_SELECTION[] _pcrAllocation)
    {
        authHandle = _authHandle;
        pcrAllocation = _pcrAllocation;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeObjArr(pcrAllocation);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _pcrAllocationCount = buf.readInt();
        pcrAllocation = new TPMS_PCR_SELECTION[_pcrAllocationCount];
        for (int j=0; j < _pcrAllocationCount; j++) pcrAllocation[j] = new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(pcrAllocation, _pcrAllocationCount);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_PCR_Allocate_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_PCR_Allocate_REQUEST ret = new TPM2_PCR_Allocate_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PCR_Allocate_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
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
    }
}

//<<<
