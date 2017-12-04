package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to cause an update to the indicated PCR.
*/
public class PCR_EventResponse extends TpmStructure
{
    /**
     * This command is used to cause an update to the indicated PCR.
     * 
     * @param _digests -
     */
    public PCR_EventResponse(TPMT_HA[] _digests)
    {
        digests = _digests;
    }
    /**
    * This command is used to cause an update to the indicated PCR.
    */
    public PCR_EventResponse() {};
    /**
    * number of digests in the list
    */
    // private int digestsCount;
    public TPMT_HA[] digests;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((digests!=null)?digests.length:0, 4);
        if(digests!=null)
            buf.writeArrayOfTpmObjects(digests);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _digestsCount = buf.readInt(4);
        digests = new TPMT_HA[_digestsCount];
        for(int j=0;j<_digestsCount;j++)digests[j]=new TPMT_HA();
        buf.readArrayOfTpmObjects(digests, _digestsCount);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static PCR_EventResponse fromTpm (byte[] x) 
    {
        PCR_EventResponse ret = new PCR_EventResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
        _p.add(d, "TpmHash", "digests", digests);
    };
    
    
};

//<<<

