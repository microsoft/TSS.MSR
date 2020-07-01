package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is the scheme data for schemes that only require a hash to complete
 *  their definition.
 */
public class TPMS_SCHEME_HASH extends TpmStructure implements TPMU_SCHEME_KEYEDHASH, TPMU_SIG_SCHEME, TPMU_KDF_SCHEME, TPMU_ASYM_SCHEME, TPMU_SIGNATURE
{
    /** The hash algorithm used to digest the message  */
    public TPM_ALG_ID hashAlg;
    
    public TPMS_SCHEME_HASH() { hashAlg = TPM_ALG_ID.NULL; }
    
    /** @param _hashAlg The hash algorithm used to digest the message  */
    public TPMS_SCHEME_HASH(TPM_ALG_ID _hashAlg) { hashAlg = _hashAlg; }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.HMAC; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { hashAlg.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { hashAlg = TPM_ALG_ID.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_HASH fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SCHEME_HASH.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SCHEME_HASH fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_HASH fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SCHEME_HASH.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_HASH");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
    }
}

//<<<
