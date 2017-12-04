package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This replaces the current endorsement primary seed (EPS) with a value from the RNG and sets the Endorsement hierarchy controls to their default initialization values: ehEnable is SET, endorsementAuth and endorsementPolicy are both set to the Empty Buffer. It will flush any resident objects (transient or persistent) in the Endorsement hierarchy and not allow objects in the hierarchy associated with the previous EPS to be loaded.
*/
public class ChangeEPSResponse extends TpmStructure
{
    /**
     * This replaces the current endorsement primary seed (EPS) with a value from the RNG and sets the Endorsement hierarchy controls to their default initialization values: ehEnable is SET, endorsementAuth and endorsementPolicy are both set to the Empty Buffer. It will flush any resident objects (transient or persistent) in the Endorsement hierarchy and not allow objects in the hierarchy associated with the previous EPS to be loaded.
     */
    public ChangeEPSResponse()
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
    public static ChangeEPSResponse fromTpm (byte[] x) 
    {
        ChangeEPSResponse ret = new ChangeEPSResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ChangeEPSResponse fromTpm (InByteBuf buf) 
    {
        ChangeEPSResponse ret = new ChangeEPSResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ChangeEPS_RESPONSE");
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

