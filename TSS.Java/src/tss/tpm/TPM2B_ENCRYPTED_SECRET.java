package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 192 Definition of TPM2B_ENCRYPTED_SECRET Structure  */
public class TPM2B_ENCRYPTED_SECRET extends TpmStructure
{
    /** Secret  */
    public byte[] secret;
    
    public TPM2B_ENCRYPTED_SECRET() {}
    
    /** @param _secret Secret  */
    public TPM2B_ENCRYPTED_SECRET(byte[] _secret) { secret = _secret; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(secret); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { secret = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_ENCRYPTED_SECRET fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_ENCRYPTED_SECRET.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_ENCRYPTED_SECRET fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_ENCRYPTED_SECRET fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_ENCRYPTED_SECRET.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_ENCRYPTED_SECRET");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "secret", secret);
    }
}

//<<<
