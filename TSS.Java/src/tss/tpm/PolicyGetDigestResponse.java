package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the current policyDigest of the session. This command allows the
 *  TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
 */
public class PolicyGetDigestResponse extends TpmStructure
{
    /** The current value of the policySessionpolicyDigest  */
    public byte[] policyDigest;
    
    public PolicyGetDigestResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(policyDigest);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _policyDigestSize = buf.readShort() & 0xFFFF;
        policyDigest = new byte[_policyDigestSize];
        buf.readArrayOfInts(policyDigest, 1, _policyDigestSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static PolicyGetDigestResponse fromBytes (byte[] byteBuf) 
    {
        PolicyGetDigestResponse ret = new PolicyGetDigestResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static PolicyGetDigestResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static PolicyGetDigestResponse fromTpm (InByteBuf buf) 
    {
        PolicyGetDigestResponse ret = new PolicyGetDigestResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyGetDigest_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "policyDigest", policyDigest);
    }
}

//<<<
