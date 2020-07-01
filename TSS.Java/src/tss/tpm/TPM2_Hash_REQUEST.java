package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs a hash operation on a data buffer and returns the results.  */
public class TPM2_Hash_REQUEST extends ReqStructure
{
    /** Data to be hashed  */
    public byte[] data;
    
    /** Algorithm for the hash being computed shall not be TPM_ALG_NULL  */
    public TPM_ALG_ID hashAlg;
    
    /** Hierarchy to use for the ticket (TPM_RH_NULL allowed)  */
    public TPM_HANDLE hierarchy;
    
    public TPM2_Hash_REQUEST()
    {
        hashAlg = TPM_ALG_ID.NULL;
        hierarchy = new TPM_HANDLE();
    }
    
    /** @param _data Data to be hashed
     *  @param _hashAlg Algorithm for the hash being computed shall not be TPM_ALG_NULL
     *  @param _hierarchy Hierarchy to use for the ticket (TPM_RH_NULL allowed)
     */
    public TPM2_Hash_REQUEST(byte[] _data, TPM_ALG_ID _hashAlg, TPM_HANDLE _hierarchy)
    {
        data = _data;
        hashAlg = _hashAlg;
        hierarchy = _hierarchy;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(data);
        hashAlg.toTpm(buf);
        hierarchy.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        data = buf.readSizedByteBuf();
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        hierarchy = TPM_HANDLE.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_Hash_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Hash_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Hash_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_Hash_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Hash_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Hash_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "data", data);
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
