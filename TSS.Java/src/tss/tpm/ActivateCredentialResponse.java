package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command enables the association of a credential with an object in a way that
 *  ensures that the TPM has validated the parameters of the credentialed object.
 */
public class ActivateCredentialResponse extends TpmStructure
{
    /** The decrypted certificate information
     *  the data should be no larger than the size of the digest of the nameAlg associated
     *  with keyHandle
     */
    public byte[] certInfo;
    
    public ActivateCredentialResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(certInfo);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _certInfoSize = buf.readShort() & 0xFFFF;
        certInfo = new byte[_certInfoSize];
        buf.readArrayOfInts(certInfo, 1, _certInfoSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static ActivateCredentialResponse fromBytes (byte[] byteBuf) 
    {
        ActivateCredentialResponse ret = new ActivateCredentialResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ActivateCredentialResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static ActivateCredentialResponse fromTpm (InByteBuf buf) 
    {
        ActivateCredentialResponse ret = new ActivateCredentialResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ActivateCredential_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "certInfo", certInfo);
    }
}

//<<<
