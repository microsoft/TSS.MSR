package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows the TPM to perform the actions required of a Certificate Authority
 *  (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
 */
public class MakeCredentialResponse extends TpmStructure
{
    /** The credential  */
    public TPMS_ID_OBJECT credentialBlob;
    
    /** Handle algorithm-dependent data that wraps the key that encrypts credentialBlob  */
    public byte[] secret;
    
    public MakeCredentialResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(credentialBlob);
        buf.writeSizedByteBuf(secret);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        credentialBlob = buf.createSizedObj(TPMS_ID_OBJECT.class);
        secret = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static MakeCredentialResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(MakeCredentialResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static MakeCredentialResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static MakeCredentialResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(MakeCredentialResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_MakeCredential_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ID_OBJECT", "credentialBlob", credentialBlob);
        _p.add(d, "byte", "secret", secret);
    }
}

//<<<
