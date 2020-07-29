package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This definition is for split signing schemes that require a commit count.  */
public class TPMS_SCHEME_ECDAA extends TpmStructure implements TPMU_SIG_SCHEME, TPMU_ASYM_SCHEME
{
    /** The hash algorithm used to digest the message  */
    public TPM_ALG_ID hashAlg;
    
    /** The counter value that is used between TPM2_Commit() and the sign operation  */
    public int count;
    
    public TPMS_SCHEME_ECDAA() { hashAlg = TPM_ALG_ID.NULL; }
    
    /** @param _hashAlg The hash algorithm used to digest the message
     *  @param _count The counter value that is used between TPM2_Commit() and the sign operation
     */
    public TPMS_SCHEME_ECDAA(TPM_ALG_ID _hashAlg, int _count)
    {
        hashAlg = _hashAlg;
        count = _count;
    }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ECDAA; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        hashAlg.toTpm(buf);
        buf.writeShort(count);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        count = buf.readShort();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_ECDAA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SCHEME_ECDAA.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SCHEME_ECDAA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_ECDAA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SCHEME_ECDAA.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_ECDAA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
        _p.add(d, "int", "count", count);
    }
}

//<<<
