package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Most of the ECC signature schemes only require a hash algorithm to complete the
 *  definition and can be typed as TPMS_SCHEME_HASH. Anonymous algorithms also require a
 *  count value so they are typed to be TPMS_SCHEME_ECDAA.
 */
public class TPMS_SCHEME_ECDSA extends TPMS_SIG_SCHEME_ECDSA
{
    public TPMS_SCHEME_ECDSA() {}
    
    /** @param _hashAlg The hash algorithm used to digest the message  */
    public TPMS_SCHEME_ECDSA(TPM_ALG_ID _hashAlg) { super(_hashAlg); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_ECDSA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SCHEME_ECDSA.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SCHEME_ECDSA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_ECDSA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SCHEME_ECDSA.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_ECDSA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<
