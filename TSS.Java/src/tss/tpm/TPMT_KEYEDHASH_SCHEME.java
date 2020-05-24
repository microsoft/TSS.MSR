package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used for a hash signing object. */
public class TPMT_KEYEDHASH_SCHEME extends TpmStructure
{
    /** selects the scheme */
    public TPM_ALG_ID scheme() { return details != null ? details.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** the scheme parameters */
    public TPMU_SCHEME_KEYEDHASH details;
    
    public TPMT_KEYEDHASH_SCHEME() {}
    
    /**
     *  @param _details the scheme parameters
     *         (One of [TPMS_SCHEME_HMAC, TPMS_SCHEME_XOR, TPMS_NULL_SCHEME_KEYEDHASH])
     */
    public TPMT_KEYEDHASH_SCHEME(TPMU_SCHEME_KEYEDHASH _details) { details = _details; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        if (details == null) return;
        details.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)details).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _scheme = buf.readShort() & 0xFFFF;
        details = UnionFactory.create("TPMU_SCHEME_KEYEDHASH", new TPM_ALG_ID(_scheme));
        details.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPMT_KEYEDHASH_SCHEME fromTpm (byte[] x) 
    {
        TPMT_KEYEDHASH_SCHEME ret = new TPMT_KEYEDHASH_SCHEME();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMT_KEYEDHASH_SCHEME fromTpm (InByteBuf buf) 
    {
        TPMT_KEYEDHASH_SCHEME ret = new TPMT_KEYEDHASH_SCHEME();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_KEYEDHASH_SCHEME");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_SCHEME_KEYEDHASH", "details", details);
    }
}

//<<<
