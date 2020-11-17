package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command adds the last part of data, if any, to a hash/HMAC sequence and returns
 *  the result.
 */
public class TPM2_SequenceComplete_REQUEST extends ReqStructure
{
    /** Authorization for the sequence
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE sequenceHandle;

    /** Data to be added to the hash/HMAC */
    public byte[] buffer;

    /** Hierarchy of the ticket for a hash */
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

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(buffer);
        hierarchy.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        buffer = buf.readSizedByteBuf();
        hierarchy = TPM_HANDLE.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_SequenceComplete_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_SequenceComplete_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_SequenceComplete_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_SequenceComplete_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_SequenceComplete_REQUEST.class);
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
        _p.add(d, "byte[]", "buffer", buffer);
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {sequenceHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
