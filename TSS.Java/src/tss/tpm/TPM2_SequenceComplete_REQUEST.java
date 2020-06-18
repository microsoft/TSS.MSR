package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command adds the last part of data, if any, to a hash/HMAC sequence and returns
 *  the result.
 */
public class TPM2_SequenceComplete_REQUEST extends TpmStructure
{
    /** Authorization for the sequence
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE sequenceHandle;
    
    /** Data to be added to the hash/HMAC  */
    public byte[] buffer;
    
    /** Hierarchy of the ticket for a hash  */
    public TPM_HANDLE hierarchy;
    
    public TPM2_SequenceComplete_REQUEST()
    {
        sequenceHandle = new TPM_HANDLE();
        hierarchy = new TPM_HANDLE();
    }
    
    /** @param _sequenceHandle Authorization for the sequence
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _buffer Data to be added to the hash/HMAC
     *  @param _hierarchy Hierarchy of the ticket for a hash
     */
    public TPM2_SequenceComplete_REQUEST(TPM_HANDLE _sequenceHandle, byte[] _buffer, TPM_HANDLE _hierarchy)
    {
        sequenceHandle = _sequenceHandle;
        buffer = _buffer;
        hierarchy = _hierarchy;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(buffer);
        hierarchy.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _bufferSize = buf.readShort() & 0xFFFF;
        buffer = new byte[_bufferSize];
        buf.readArrayOfInts(buffer, 1, _bufferSize);
        hierarchy = TPM_HANDLE.fromTpm(buf);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_SequenceComplete_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_SequenceComplete_REQUEST ret = new TPM2_SequenceComplete_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_SequenceComplete_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
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
    }
}

//<<<
