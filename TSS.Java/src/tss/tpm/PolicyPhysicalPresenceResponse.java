package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command indicates that physical presence will need to be asserted at the time the authorization is performed.
*/
public class PolicyPhysicalPresenceResponse extends TpmStructure
{
    /**
     * This command indicates that physical presence will need to be asserted at the time the authorization is performed.
     */
    public PolicyPhysicalPresenceResponse()
    {
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static PolicyPhysicalPresenceResponse fromTpm (byte[] x) 
    {
        PolicyPhysicalPresenceResponse ret = new PolicyPhysicalPresenceResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PolicyPhysicalPresenceResponse fromTpm (InByteBuf buf) 
    {
        PolicyPhysicalPresenceResponse ret = new PolicyPhysicalPresenceResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyPhysicalPresence_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
    };
    
    
};

//<<<

