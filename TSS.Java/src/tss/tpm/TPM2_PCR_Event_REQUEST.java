package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to cause an update to the indicated PCR.
*/
public class TPM2_PCR_Event_REQUEST extends TpmStructure
{
    /**
     * This command is used to cause an update to the indicated PCR.
     * 
     * @param _pcrHandle Handle of the PCR Auth Handle: 1 Auth Role: USER 
     * @param _eventData Event data in sized buffer
     */
    public TPM2_PCR_Event_REQUEST(TPM_HANDLE _pcrHandle,byte[] _eventData)
    {
        pcrHandle = _pcrHandle;
        eventData = _eventData;
    }
    /**
    * This command is used to cause an update to the indicated PCR.
    */
    public TPM2_PCR_Event_REQUEST() {};
    /**
    * Handle of the PCR Auth Handle: 1 Auth Role: USER
    */
    public TPM_HANDLE pcrHandle;
    /**
    * size of the operand buffer
    */
    // private short eventDataSize;
    /**
    * Event data in sized buffer
    */
    public byte[] eventData;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        pcrHandle.toTpm(buf);
        buf.writeInt((eventData!=null)?eventData.length:0, 2);
        if(eventData!=null)
            buf.write(eventData);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        pcrHandle = TPM_HANDLE.fromTpm(buf);
        int _eventDataSize = buf.readInt(2);
        eventData = new byte[_eventDataSize];
        buf.readArrayOfInts(eventData, 1, _eventDataSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PCR_Event_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PCR_Event_REQUEST ret = new TPM2_PCR_Event_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
    };
    
    
};

//<<<

