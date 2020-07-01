package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used in TPM2_ContextLoad() and TPM2_ContextSave(). If the values of
 *  the TPMS_CONTEXT structure in TPM2_ContextLoad() are not the same as the values when
 *  the context was saved (TPM2_ContextSave()), then the TPM shall not load the context.
 */
public class TPMS_CONTEXT extends TpmStructure
{
    /** The sequence number of the context
     *  NOTE Transient object contexts and session contexts used different counters.
     */
    public long sequence;
    
    /** A handle indicating if the context is a session, object, or sequence object (see Table
     *  222 Context Handle Values
     */
    public TPM_HANDLE savedHandle;
    
    /** The hierarchy of the context  */
    public TPM_HANDLE hierarchy;
    
    /** The context data and integrity HMAC  */
    public TPMS_CONTEXT_DATA contextBlob;
    
    public TPMS_CONTEXT()
    {
        savedHandle = new TPM_HANDLE();
        hierarchy = new TPM_HANDLE();
    }
    
    /** @param _sequence The sequence number of the context
     *         NOTE Transient object contexts and session contexts used different counters.
     *  @param _savedHandle A handle indicating if the context is a session, object, or sequence
     *         object (see Table 222 Context Handle Values
     *  @param _hierarchy The hierarchy of the context
     *  @param _contextBlob The context data and integrity HMAC
     */
    public TPMS_CONTEXT(long _sequence, TPM_HANDLE _savedHandle, TPM_HANDLE _hierarchy, TPMS_CONTEXT_DATA _contextBlob)
    {
        sequence = _sequence;
        savedHandle = _savedHandle;
        hierarchy = _hierarchy;
        contextBlob = _contextBlob;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeInt64(sequence);
        savedHandle.toTpm(buf);
        hierarchy.toTpm(buf);
        buf.writeSizedObj(contextBlob);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        sequence = buf.readInt64();
        savedHandle = TPM_HANDLE.fromTpm(buf);
        hierarchy = TPM_HANDLE.fromTpm(buf);
        contextBlob = buf.createSizedObj(TPMS_CONTEXT_DATA.class);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_CONTEXT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_CONTEXT.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_CONTEXT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_CONTEXT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_CONTEXT.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CONTEXT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "long", "sequence", sequence);
        _p.add(d, "TPM_HANDLE", "savedHandle", savedHandle);
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
        _p.add(d, "TPMS_CONTEXT_DATA", "contextBlob", contextBlob);
    }
}

//<<<
