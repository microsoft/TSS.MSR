package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 174 Definition of {RSA} TPMT_RSA_DECRYPT Structure */
public class TPMT_RSA_DECRYPT extends TpmStructure
{
    public TPM_ALG_ID scheme() { return details != null ? details.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** scheme parameters */
    public TPMU_ASYM_SCHEME details;
    
    public TPMT_RSA_DECRYPT() {}
    
    /**
     *  @param _details scheme parameters
     *         (One of [TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2,
     *         TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP,
     *         TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME])
     */
    public TPMT_RSA_DECRYPT(TPMU_ASYM_SCHEME _details) { details = _details; }
    
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
        details = UnionFactory.create("TPMU_ASYM_SCHEME", new TPM_ALG_ID(_scheme));
        details.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPMT_RSA_DECRYPT fromTpm (byte[] x) 
    {
        TPMT_RSA_DECRYPT ret = new TPMT_RSA_DECRYPT();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMT_RSA_DECRYPT fromTpm (InByteBuf buf) 
    {
        TPMT_RSA_DECRYPT ret = new TPMT_RSA_DECRYPT();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_RSA_DECRYPT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_ASYM_SCHEME", "details", details);
    }
}

//<<<
