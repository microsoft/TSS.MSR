package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns the current policyDigest of the session. This command allows the TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
*/
public class PolicyGetDigestResponse extends TpmStructure
{
    /**
     * This command returns the current policyDigest of the session. This command allows the TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
     * 
     * @param _policyDigest the current value of the policySessionpolicyDigest
     */
    public PolicyGetDigestResponse(byte[] _policyDigest)
    {
        policyDigest = _policyDigest;
    }
    /**
    * This command returns the current policyDigest of the session. This command allows the TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
    */
    public PolicyGetDigestResponse() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short policyDigestSize;
    /**
    * the current value of the policySessionpolicyDigest
    */
    public byte[] policyDigest;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((policyDigest!=null)?policyDigest.length:0, 2);
        if(policyDigest!=null)
            buf.write(policyDigest);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _policyDigestSize = buf.readInt(2);
        policyDigest = new byte[_policyDigestSize];
        buf.readArrayOfInts(policyDigest, 1, _policyDigestSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static PolicyGetDigestResponse fromTpm (byte[] x) 
    {
        PolicyGetDigestResponse ret = new PolicyGetDigestResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
    };
    
    
};

//<<<

