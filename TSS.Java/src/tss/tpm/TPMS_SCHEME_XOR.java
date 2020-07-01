package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is for the XOR encryption scheme.  */
public class TPMS_SCHEME_XOR extends TpmStructure implements TPMU_SCHEME_KEYEDHASH
{
    /** The hash algorithm used to digest the message  */
    public TPM_ALG_ID hashAlg;
    
    /** The key derivation function  */
    public TPM_ALG_ID kdf;
    
    public TPMS_SCHEME_XOR()
    {
        hashAlg = TPM_ALG_ID.NULL;
        kdf = TPM_ALG_ID.NULL;
    }
    
    /** @param _hashAlg The hash algorithm used to digest the message
     *  @param _kdf The key derivation function
     */
    public TPMS_SCHEME_XOR(TPM_ALG_ID _hashAlg, TPM_ALG_ID _kdf)
    {
        hashAlg = _hashAlg;
        kdf = _kdf;
    }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.XOR; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        hashAlg.toTpm(buf);
        kdf.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        kdf = TPM_ALG_ID.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_XOR fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SCHEME_XOR.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SCHEME_XOR fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_XOR fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SCHEME_XOR.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_XOR");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
        _p.add(d, "TPM_ALG_ID", "kdf", kdf);
    }
}

//<<<
