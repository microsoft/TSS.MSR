package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the current policyDigest of the session. This command allows the
 *  TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
 */
public class PolicyGetDigestResponse extends RespStructure
{
    /** The current value of the policySessionpolicyDigest  */
    public byte[] policyDigest;
    
    public PolicyGetDigestResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(policyDigest); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { policyDigest = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static PolicyGetDigestResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(PolicyGetDigestResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static PolicyGetDigestResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static PolicyGetDigestResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(PolicyGetDigestResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("PolicyGetDigestResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "policyDigest", policyDigest);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
