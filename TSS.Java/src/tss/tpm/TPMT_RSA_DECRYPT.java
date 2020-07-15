package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 174 Definition of {RSA} TPMT_RSA_DECRYPT Structure  */
public class TPMT_RSA_DECRYPT extends TpmStructure
{
    /** Scheme selector  */
    public TPM_ALG_ID scheme() { return details != null ? details.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** Scheme parameters
     *  One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *  TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *  TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *  TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
     */
    public TPMU_ASYM_SCHEME details;
    
    public TPMT_RSA_DECRYPT() {}
    
    /** @param _details Scheme parameters
     *         One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *         TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *         TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
     */
    public TPMT_RSA_DECRYPT(TPMU_ASYM_SCHEME _details) { details = _details; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        if (details == null) return;
        buf.writeShort(details.GetUnionSelector());
        details.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        TPM_ALG_ID scheme = TPM_ALG_ID.fromTpm(buf);
        details = UnionFactory.create("TPMU_ASYM_SCHEME", scheme);
        details.initFromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMT_RSA_DECRYPT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_RSA_DECRYPT.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_RSA_DECRYPT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMT_RSA_DECRYPT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_RSA_DECRYPT.class);
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
