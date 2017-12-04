package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command uses platformPolicy and a TPM Vendor Authorization Key to authorize a Field Upgrade Manifest.
*/
public class FieldUpgradeStartResponse extends TpmStructure
{
    /**
     * This command uses platformPolicy and a TPM Vendor Authorization Key to authorize a Field Upgrade Manifest.
     */
    public FieldUpgradeStartResponse()
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
    public static FieldUpgradeStartResponse fromTpm (byte[] x) 
    {
        FieldUpgradeStartResponse ret = new FieldUpgradeStartResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static FieldUpgradeStartResponse fromTpm (InByteBuf buf) 
    {
        FieldUpgradeStartResponse ret = new FieldUpgradeStartResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FieldUpgradeStart_RESPONSE");
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

