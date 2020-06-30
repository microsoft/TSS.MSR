package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These are the ECC schemes that only need a hash algorithm as a controlling parameter.  */
public class TPMS_SCHEME_ECDH extends TPMS_KEY_SCHEME_ECDH
{
    public TPMS_SCHEME_ECDH() {}
    
    /** @param _hashAlg The hash algorithm used to digest the message  */
    public TPMS_SCHEME_ECDH(TPM_ALG_ID _hashAlg) { super(_hashAlg); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_ECDH fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SCHEME_ECDH.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SCHEME_ECDH fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_ECDH fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SCHEME_ECDH.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_ECDH");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<
