package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command adds the last part of data, if any, to an Event Sequence and returns the
 *  result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the
 *  returned digest list is processed in the same manner as the digest list input
 *  parameter to TPM2_PCR_Extend(). That is, if a bank contains a PCR associated with
 *  pcrHandle, it is extended with the associated digest value from the list.
 */
public class TPM2_EventSequenceComplete_REQUEST extends ReqStructure
{
    /** PCR to be extended with the Event data
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE pcrHandle;
    
    /** Authorization for the sequence
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE sequenceHandle;
    
    /** Data to be added to the Event  */
    public byte[] buffer;
    
    public TPM2_EventSequenceComplete_REQUEST()
    {
        pcrHandle = new TPM_HANDLE();
        sequenceHandle = new TPM_HANDLE();
    }
    
    /** @param _pcrHandle PCR to be extended with the Event data
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _sequenceHandle Authorization for the sequence
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _buffer Data to be added to the Event
     */
    public TPM2_EventSequenceComplete_REQUEST(TPM_HANDLE _pcrHandle, TPM_HANDLE _sequenceHandle, byte[] _buffer)
    {
        pcrHandle = _pcrHandle;
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
    public static TPM2_EventSequenceComplete_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_EventSequenceComplete_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_EventSequenceComplete_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_EventSequenceComplete_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_EventSequenceComplete_REQUEST.class);
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
        _p.add(d, "byte[]", "buffer", buffer);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 2; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {pcrHandle, sequenceHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
