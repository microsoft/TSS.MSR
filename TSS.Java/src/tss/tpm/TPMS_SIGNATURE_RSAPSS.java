package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 185 Definition of {RSA} TPMS_SIGNATURE_RSA Structure  */
public class TPMS_SIGNATURE_RSAPSS extends TPMS_SIGNATURE_RSA
{
    public TPMS_SIGNATURE_RSAPSS() {}
    
    /** @param _hash The hash algorithm used to digest the message
     *         TPM_ALG_NULL is not allowed.
     *  @param _sig The signature is the size of a public key.
     */
    public TPMS_SIGNATURE_RSAPSS(TPM_ALG_ID _hash, byte[] _sig)
    {
        super(_hash, _sig);
    }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.RSAPSS; }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_SIGNATURE_RSAPSS fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SIGNATURE_RSAPSS.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SIGNATURE_RSAPSS fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_SIGNATURE_RSAPSS fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SIGNATURE_RSAPSS.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIGNATURE_RSAPSS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<
