package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to cause an update to the indicated PCR.  */
public class PCR_EventResponse extends TpmStructure
{
    public TPMT_HA[] digests;
    
    public PCR_EventResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeObjArr(digests);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _digestsCount = buf.readInt();
        digests = new TPMT_HA[_digestsCount];
        for (int j=0; j < _digestsCount; j++) digests[j] = new TPMT_HA();
        buf.readArrayOfTpmObjects(digests, _digestsCount);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static PCR_EventResponse fromBytes (byte[] byteBuf) 
    {
        PCR_EventResponse ret = new PCR_EventResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static PCR_EventResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static PCR_EventResponse fromTpm (InByteBuf buf) 
    {
        PCR_EventResponse ret = new PCR_EventResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Event_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_HA", "digests", digests);
    }
}

//<<<
