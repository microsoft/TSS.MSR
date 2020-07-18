package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs RSA decryption using the indicated padding scheme according to
 *  IETF RFC 8017 ((PKCS#1).
 */
public class RSA_DecryptResponse extends RespStructure
{
    /** Decrypted output  */
    public byte[] message;
    
    public RSA_DecryptResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(message); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { message = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static RSA_DecryptResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(RSA_DecryptResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static RSA_DecryptResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static RSA_DecryptResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(RSA_DecryptResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("RSA_DecryptResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "message", message);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
