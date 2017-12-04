package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command adds the last part of data, if any, to an Event Sequence and returns the result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the returned digest list is processed in the same manner as the digest list input parameter to TPM2_PCR_Extend() with the pcrHandle in each bank extended with the associated digest value.
*/
public class TPM2_EventSequenceComplete_REQUEST extends TpmStructure
{
    /**
     * This command adds the last part of data, if any, to an Event Sequence and returns the result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the returned digest list is processed in the same manner as the digest list input parameter to TPM2_PCR_Extend() with the pcrHandle in each bank extended with the associated digest value.
     * 
     * @param _pcrHandle PCR to be extended with the Event data Auth Index: 1 Auth Role: USER 
     * @param _sequenceHandle authorization for the sequence Auth Index: 2 Auth Role: USER 
     * @param _buffer data to be added to the Event
     */
    public TPM2_EventSequenceComplete_REQUEST(TPM_HANDLE _pcrHandle,TPM_HANDLE _sequenceHandle,byte[] _buffer)
    {
        pcrHandle = _pcrHandle;
        sequenceHandle = _sequenceHandle;
        buffer = _buffer;
    }
    /**
    * This command adds the last part of data, if any, to an Event Sequence and returns the result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the returned digest list is processed in the same manner as the digest list input parameter to TPM2_PCR_Extend() with the pcrHandle in each bank extended with the associated digest value.
    */
    public TPM2_EventSequenceComplete_REQUEST() {};
    /**
    * PCR to be extended with the Event data Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE pcrHandle;
    /**
    * authorization for the sequence Auth Index: 2 Auth Role: USER
    */
    public TPM_HANDLE sequenceHandle;
    /**
    * size of the buffer
    */
    // private short bufferSize;
    /**
    * data to be added to the Event
    */
    public byte[] buffer;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        pcrHandle.toTpm(buf);
        sequenceHandle.toTpm(buf);
        buf.writeInt((buffer!=null)?buffer.length:0, 2);
        if(buffer!=null)
            buf.write(buffer);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        pcrHandle = TPM_HANDLE.fromTpm(buf);
        sequenceHandle = TPM_HANDLE.fromTpm(buf);
        int _bufferSize = buf.readInt(2);
        buffer = new byte[_bufferSize];
        buf.readArrayOfInts(buffer, 1, _bufferSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_EventSequenceComplete_REQUEST fromTpm (byte[] x) 
    {
        TPM2_EventSequenceComplete_REQUEST ret = new TPM2_EventSequenceComplete_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_EventSequenceComplete_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_EventSequenceComplete_REQUEST ret = new TPM2_EventSequenceComplete_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EventSequenceComplete_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "pcrHandle", pcrHandle);
        _p.add(d, "TPM_HANDLE", "sequenceHandle", sequenceHandle);
        _p.add(d, "byte", "buffer", buffer);
    };
    
    
};

//<<<

