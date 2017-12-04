package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command enables and disables use of a hierarchy and its associated NV storage. The command allows phEnable, phEnableNV, shEnable, and ehEnable to be changed when the proper authorization is provided.
*/
public class HierarchyControlResponse extends TpmStructure
{
    /**
     * This command enables and disables use of a hierarchy and its associated NV storage. The command allows phEnable, phEnableNV, shEnable, and ehEnable to be changed when the proper authorization is provided.
     */
    public HierarchyControlResponse()
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
    public static HierarchyControlResponse fromTpm (byte[] x) 
    {
        HierarchyControlResponse ret = new HierarchyControlResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static HierarchyControlResponse fromTpm (InByteBuf buf) 
    {
        HierarchyControlResponse ret = new HierarchyControlResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HierarchyControl_RESPONSE");
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

