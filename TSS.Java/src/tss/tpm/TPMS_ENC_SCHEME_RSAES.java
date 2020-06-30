package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These are the RSA encryption schemes that only need a hash algorithm as a controlling parameter.  */
public class TPMS_ENC_SCHEME_RSAES extends TPMS_EMPTY
{
    public TPMS_ENC_SCHEME_RSAES() {}
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.RSAES; }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_ENC_SCHEME_RSAES fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_ENC_SCHEME_RSAES.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_ENC_SCHEME_RSAES fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_ENC_SCHEME_RSAES fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_ENC_SCHEME_RSAES.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ENC_SCHEME_RSAES");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<
