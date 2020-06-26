package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs RSA encryption using the indicated padding scheme according to
 *  IETF RFC 8017. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use
 *  inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL,
 *  then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
 */
public class RSA_EncryptResponse extends TpmStructure
{
    /** Encrypted output  */
    public byte[] outData;
    
    public RSA_EncryptResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(outData); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { outData = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static RSA_EncryptResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(RSA_EncryptResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static RSA_EncryptResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static RSA_EncryptResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(RSA_EncryptResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_RSA_Encrypt_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "outData", outData);
    }
}

//<<<
