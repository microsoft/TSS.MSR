package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to add data to a hash or HMAC sequence. The amount of data in
 *  buffer may be any size up to the limits of the TPM.
 */
public class TPM2_SequenceUpdate_REQUEST extends TpmStructure
{
    /** Handle for the sequence object
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE sequenceHandle;
    
    /** Data to be added to hash  */
    public byte[] buffer;
    
    public TPM2_SequenceUpdate_REQUEST() { sequenceHandle = new TPM_HANDLE(); }
    
    /** @param _sequenceHandle Handle for the sequence object
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _buffer Data to be added to hash
     */
    public TPM2_SequenceUpdate_REQUEST(TPM_HANDLE _sequenceHandle, byte[] _buffer)
    {
        sequenceHandle = _sequenceHandle;
        buffer = _buffer;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(buffer);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _bufferSize = buf.readShort() & 0xFFFF;
        buffer = new byte[_bufferSize];
        buf.readArrayOfInts(buffer, 1, _bufferSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_SequenceUpdate_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_SequenceUpdate_REQUEST ret = new TPM2_SequenceUpdate_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_SequenceUpdate_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
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
    }
}

//<<<
