package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is defined for coding purposes. For IO to the TPM, the sensitive
 *  portion of the key will be in a canonical form. For an RSA key, this will be one of
 *  the prime factors of the public modulus. After loading, it is typical that other
 *  values will be computed so that computations using the private key will not need to
 *  start with just one prime factor. This structure can be used to store the results of
 *  such vendor-specific calculations.
 */
public class TPM2B_PRIVATE_VENDOR_SPECIFIC extends TpmStructure implements TPMU_SENSITIVE_COMPOSITE
{
    public byte[] buffer;
    
    public TPM2B_PRIVATE_VENDOR_SPECIFIC() {}
    
    /** @param _buffer TBD  */
    public TPM2B_PRIVATE_VENDOR_SPECIFIC(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ANY; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_PRIVATE_VENDOR_SPECIFIC fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_PRIVATE_VENDOR_SPECIFIC.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_PRIVATE_VENDOR_SPECIFIC fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_PRIVATE_VENDOR_SPECIFIC fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_PRIVATE_VENDOR_SPECIFIC.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_PRIVATE_VENDOR_SPECIFIC");
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
