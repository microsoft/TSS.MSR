package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to add data to a hash or HMAC sequence. The amount of data in
 *  buffer may be any size up to the limits of the TPM.
 */
public class TPM2_SequenceUpdate_REQUEST extends ReqStructure
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
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_SequenceUpdate_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_SequenceUpdate_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_SequenceUpdate_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_SequenceUpdate_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_SequenceUpdate_REQUEST.class);
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

    @Override
    public int numHandles() { return 1; }
    
    public int numAuthHandles() { return 1; }
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {sequenceHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
