package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used in TPM2_ContextLoad() and TPM2_ContextSave(). If the values of the TPMS_CONTEXT structure in TPM2_ContextLoad() are not the same as the values when the context was saved (TPM2_ContextSave()), then the TPM shall not load the context.
*/
public class TPMS_CONTEXT extends TpmStructure
{
    /**
     * This structure is used in TPM2_ContextLoad() and TPM2_ContextSave(). If the values of the TPMS_CONTEXT structure in TPM2_ContextLoad() are not the same as the values when the context was saved (TPM2_ContextSave()), then the TPM shall not load the context.
     * 
     * @param _sequence the sequence number of the context NOTE Transient object contexts and session contexts used different counters. 
     * @param _savedHandle a handle indicating if the context is a session, object, or sequence object (see Table 212) 
     * @param _hierarchy the hierarchy of the context 
     * @param _contextBlob the context data and integrity HMAC
     */
    public TPMS_CONTEXT(long _sequence,TPM_HANDLE _savedHandle,TPM_HANDLE _hierarchy,TPMS_CONTEXT_DATA _contextBlob)
    {
        sequence = _sequence;
        savedHandle = _savedHandle;
        hierarchy = _hierarchy;
        contextBlob = _contextBlob;
    }
    /**
    * This structure is used in TPM2_ContextLoad() and TPM2_ContextSave(). If the values of the TPMS_CONTEXT structure in TPM2_ContextLoad() are not the same as the values when the context was saved (TPM2_ContextSave()), then the TPM shall not load the context.
    */
    public TPMS_CONTEXT() {};
    /**
    * the sequence number of the context NOTE Transient object contexts and session contexts used different counters.
    */
    public long sequence;
    /**
    * a handle indicating if the context is a session, object, or sequence object (see Table 212)
    */
    public TPM_HANDLE savedHandle;
    /**
    * the hierarchy of the context
    */
    public TPM_HANDLE hierarchy;
    // private short contextBlobSize;
    /**
    * the context data and integrity HMAC
    */
    public TPMS_CONTEXT_DATA contextBlob;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.write(sequence);
        savedHandle.toTpm(buf);
        hierarchy.toTpm(buf);
        buf.writeInt((contextBlob!=null)?contextBlob.toTpm().length:0, 2);
        if(contextBlob!=null)
            contextBlob.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        sequence = buf.readLong();
        savedHandle = TPM_HANDLE.fromTpm(buf);
        hierarchy = TPM_HANDLE.fromTpm(buf);
        int _contextBlobSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _contextBlobSize));
        contextBlob = TPMS_CONTEXT_DATA.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_CONTEXT fromTpm (byte[] x) 
    {
        TPMS_CONTEXT ret = new TPMS_CONTEXT();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_CONTEXT fromTpm (InByteBuf buf) 
    {
        TPMS_CONTEXT ret = new TPMS_CONTEXT();
        ret.initFromTpm(buf);
        return ret;
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
        _p.add(d, "ulong", "sequence", sequence);
        _p.add(d, "TPM_HANDLE", "savedHandle", savedHandle);
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
        _p.add(d, "TPMS_CONTEXT_DATA", "contextBlob", contextBlob);
    };
    
    
};

//<<<

