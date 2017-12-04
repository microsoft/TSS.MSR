package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
*/
public class TPM2_SequenceComplete_REQUEST extends TpmStructure
{
    /**
     * This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
     * 
     * @param _sequenceHandle authorization for the sequence Auth Index: 1 Auth Role: USER 
     * @param _buffer data to be added to the hash/HMAC 
     * @param _hierarchy hierarchy of the ticket for a hash
     */
    public TPM2_SequenceComplete_REQUEST(TPM_HANDLE _sequenceHandle,byte[] _buffer,TPM_HANDLE _hierarchy)
    {
        sequenceHandle = _sequenceHandle;
        buffer = _buffer;
        hierarchy = _hierarchy;
    }
    /**
    * This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
    */
    public TPM2_SequenceComplete_REQUEST() {};
    /**
    * authorization for the sequence Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE sequenceHandle;
    /**
    * size of the buffer
    */
    // private short bufferSize;
    /**
    * data to be added to the hash/HMAC
    */
    public byte[] buffer;
    /**
    * hierarchy of the ticket for a hash
    */
    public TPM_HANDLE hierarchy;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        sequenceHandle.toTpm(buf);
        buf.writeInt((buffer!=null)?buffer.length:0, 2);
        if(buffer!=null)
            buf.write(buffer);
        hierarchy.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        sequenceHandle = TPM_HANDLE.fromTpm(buf);
        int _bufferSize = buf.readInt(2);
        buffer = new byte[_bufferSize];
        buf.readArrayOfInts(buffer, 1, _bufferSize);
        hierarchy = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_SequenceComplete_REQUEST fromTpm (byte[] x) 
    {
        TPM2_SequenceComplete_REQUEST ret = new TPM2_SequenceComplete_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_SequenceComplete_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_SequenceComplete_REQUEST ret = new TPM2_SequenceComplete_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SequenceComplete_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "sequenceHandle", sequenceHandle);
        _p.add(d, "byte", "buffer", buffer);
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
    };
    
    
};

//<<<

