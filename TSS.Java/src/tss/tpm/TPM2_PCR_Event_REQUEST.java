package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to cause an update to the indicated PCR.  */
public class TPM2_PCR_Event_REQUEST extends TpmStructure
{
    /** Handle of the PCR
     *  Auth Handle: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE pcrHandle;
    
    /** Event data in sized buffer  */
    public byte[] eventData;
    
    public TPM2_PCR_Event_REQUEST() { pcrHandle = new TPM_HANDLE(); }
    
    /** @param _pcrHandle Handle of the PCR
     *         Auth Handle: 1
     *         Auth Role: USER
     *  @param _eventData Event data in sized buffer
     */
    public TPM2_PCR_Event_REQUEST(TPM_HANDLE _pcrHandle, byte[] _eventData)
    {
        pcrHandle = _pcrHandle;
        eventData = _eventData;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(eventData);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _eventDataSize = buf.readShort() & 0xFFFF;
        eventData = new byte[_eventDataSize];
        buf.readArrayOfInts(eventData, 1, _eventDataSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_PCR_Event_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_PCR_Event_REQUEST ret = new TPM2_PCR_Event_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PCR_Event_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_PCR_Event_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PCR_Event_REQUEST ret = new TPM2_PCR_Event_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Event_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "pcrHandle", pcrHandle);
        _p.add(d, "byte", "eventData", eventData);
    }
}

//<<<
