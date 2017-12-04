package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command adds the last part of data, if any, to an Event Sequence and returns the result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the returned digest list is processed in the same manner as the digest list input parameter to TPM2_PCR_Extend() with the pcrHandle in each bank extended with the associated digest value.
*/
public class EventSequenceCompleteResponse extends TpmStructure
{
    /**
     * This command adds the last part of data, if any, to an Event Sequence and returns the result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the returned digest list is processed in the same manner as the digest list input parameter to TPM2_PCR_Extend() with the pcrHandle in each bank extended with the associated digest value.
     * 
     * @param _results list of digests computed for the PCR
     */
    public EventSequenceCompleteResponse(TPMT_HA[] _results)
    {
        results = _results;
    }
    /**
    * This command adds the last part of data, if any, to an Event Sequence and returns the result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the returned digest list is processed in the same manner as the digest list input parameter to TPM2_PCR_Extend() with the pcrHandle in each bank extended with the associated digest value.
    */
    public EventSequenceCompleteResponse() {};
    /**
    * number of digests in the list
    */
    // private int resultsCount;
    /**
    * list of digests computed for the PCR
    */
    public TPMT_HA[] results;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((results!=null)?results.length:0, 4);
        if(results!=null)
            buf.writeArrayOfTpmObjects(results);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _resultsCount = buf.readInt(4);
        results = new TPMT_HA[_resultsCount];
        for(int j=0;j<_resultsCount;j++)results[j]=new TPMT_HA();
        buf.readArrayOfTpmObjects(results, _resultsCount);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static EventSequenceCompleteResponse fromTpm (byte[] x) 
    {
        EventSequenceCompleteResponse ret = new EventSequenceCompleteResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static EventSequenceCompleteResponse fromTpm (InByteBuf buf) 
    {
        EventSequenceCompleteResponse ret = new EventSequenceCompleteResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EventSequenceComplete_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TpmHash", "results", results);
    };
    
    
};

//<<<

