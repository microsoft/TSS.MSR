package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to add data to a hash or HMAC sequence. The amount of data in buffer may be any size up to the limits of the TPM.
*/
public class TPM2_SequenceUpdate_REQUEST extends TpmStructure
{
    /**
     * This command is used to add data to a hash or HMAC sequence. The amount of data in buffer may be any size up to the limits of the TPM.
     * 
     * @param _sequenceHandle handle for the sequence object Auth Index: 1 Auth Role: USER 
     * @param _buffer data to be added to hash
     */
    public TPM2_SequenceUpdate_REQUEST(TPM_HANDLE _sequenceHandle,byte[] _buffer)
    {
        sequenceHandle = _sequenceHandle;
        buffer = _buffer;
    }
    /**
    * This command is used to add data to a hash or HMAC sequence. The amount of data in buffer may be any size up to the limits of the TPM.
    */
    public TPM2_SequenceUpdate_REQUEST() {};
    /**
    * handle for the sequence object Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE sequenceHandle;
    /**
    * size of the buffer
    */
    // private short bufferSize;
    /**
    * data to be added to hash
    */
    public byte[] buffer;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        sequenceHandle.toTpm(buf);
        buf.writeInt((buffer!=null)?buffer.length:0, 2);
        if(buffer!=null)
            buf.write(buffer);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
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
    public static TPM2_SequenceUpdate_REQUEST fromTpm (byte[] x) 
    {
        TPM2_SequenceUpdate_REQUEST ret = new TPM2_SequenceUpdate_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_SequenceUpdate_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_SequenceUpdate_REQUEST ret = new TPM2_SequenceUpdate_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SequenceUpdate_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "sequenceHandle", sequenceHandle);
        _p.add(d, "byte", "buffer", buffer);
    };
    
    
};

//<<<

