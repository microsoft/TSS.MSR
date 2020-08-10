package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 185 Definition of {RSA} TPMS_SIGNATURE_RSA Structure  */
public class TPMS_SIGNATURE_RSA extends TpmStructure implements TPMU_SIGNATURE
{
    /** The hash algorithm used to digest the message
     *  TPM_ALG_NULL is not allowed.
     */
    public TPM_ALG_ID hash;
    
    /** The signature is the size of a public key.  */
    public byte[] sig;
    
    public TPMS_SIGNATURE_RSA() { hash = TPM_ALG_ID.NULL; }
    
    /** @param _hash The hash algorithm used to digest the message
     *         TPM_ALG_NULL is not allowed.
     *  @param _sig The signature is the size of a public key.
     */
    public TPMS_SIGNATURE_RSA(TPM_ALG_ID _hash, byte[] _sig)
    {
        hash = _hash;
        sig = _sig;
    }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.RSASSA; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        hash.toTpm(buf);
        buf.writeSizedByteBuf(sig);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        hash = TPM_ALG_ID.fromTpm(buf);
        sig = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_SIGNATURE_RSA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SIGNATURE_RSA.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SIGNATURE_RSA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_SIGNATURE_RSA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SIGNATURE_RSA.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIGNATURE_RSA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hash", hash);
        _p.add(d, "byte[]", "sig", sig);
    }
}

//<<<
