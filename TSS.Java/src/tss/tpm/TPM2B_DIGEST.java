package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used for a sized buffer that cannot be larger than the largest
 *  digest produced by any hash algorithm implemented on the TPM.
 */
public class TPM2B_DIGEST extends TpmStructure implements TPMU_PUBLIC_ID
{
    /** The buffer area that can be no larger than a digest  */
    public byte[] buffer;
    
    public TPM2B_DIGEST() {}
    
    /** @param _buffer The buffer area that can be no larger than a digest  */
    public TPM2B_DIGEST(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.KEYEDHASH; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_DIGEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_DIGEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_DIGEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_DIGEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_DIGEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_DIGEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "buffer", buffer);
    }
}

//<<<
