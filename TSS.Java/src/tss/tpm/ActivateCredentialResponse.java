package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command enables the association of a credential with an object in a way that
 *  ensures that the TPM has validated the parameters of the credentialed object.
 */
public class ActivateCredentialResponse extends RespStructure
{
    /** The decrypted certificate information
     *  the data should be no larger than the size of the digest of the nameAlg associated
     *  with keyHandle
     */
    public byte[] certInfo;
    
    public ActivateCredentialResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(certInfo); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { certInfo = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static ActivateCredentialResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ActivateCredentialResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ActivateCredentialResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static ActivateCredentialResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ActivateCredentialResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("ActivateCredentialResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "certInfo", certInfo);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
