package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 162 Definition of TPMT_SIG_SCHEME Structure */
public class TPMT_SIG_SCHEME extends TpmStructure
{
    /** scheme selector */
    public TPM_ALG_ID scheme() { return details != null ? details.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** scheme parameters */
    public TPMU_SIG_SCHEME details;
    
    public TPMT_SIG_SCHEME() {}
    
    /**
     *  @param _details scheme parameters
     *         (One of [TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME])
     */
    public TPMT_SIG_SCHEME(TPMU_SIG_SCHEME _details) { details = _details; }
    
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
        details = UnionFactory.create("TPMU_SIG_SCHEME", new TPM_ALG_ID(_scheme));
        details.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPMT_SIG_SCHEME fromTpm (byte[] x) 
    {
        TPMT_SIG_SCHEME ret = new TPMT_SIG_SCHEME();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMT_SIG_SCHEME fromTpm (InByteBuf buf) 
    {
        TPMT_SIG_SCHEME ret = new TPMT_SIG_SCHEME();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SIG_SCHEME");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_SIG_SCHEME", "details", details);
    }
}

//<<<
