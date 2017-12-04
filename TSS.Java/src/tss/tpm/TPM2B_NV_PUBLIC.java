package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used when a TPMS_NV_PUBLIC is sent on the TPM interface.
*/
public class TPM2B_NV_PUBLIC extends TpmStructure
{
    /**
     * This structure is used when a TPMS_NV_PUBLIC is sent on the TPM interface.
     * 
     * @param _nvPublic the public area
     */
    public TPM2B_NV_PUBLIC(TPMS_NV_PUBLIC _nvPublic)
    {
        nvPublic = _nvPublic;
    }
    /**
    * This structure is used when a TPMS_NV_PUBLIC is sent on the TPM interface.
    */
    public TPM2B_NV_PUBLIC() {};
    /**
    * size of nvPublic
    */
    // private short size;
    /**
    * the public area
    */
    public TPMS_NV_PUBLIC nvPublic;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((nvPublic!=null)?nvPublic.toTpm().length:0, 2);
        if(nvPublic!=null)
            nvPublic.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _size));
        nvPublic = TPMS_NV_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2B_NV_PUBLIC fromTpm (byte[] x) 
    {
        TPM2B_NV_PUBLIC ret = new TPM2B_NV_PUBLIC();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_NV_PUBLIC fromTpm (InByteBuf buf) 
    {
        TPM2B_NV_PUBLIC ret = new TPM2B_NV_PUBLIC();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_NV_PUBLIC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_NV_PUBLIC", "nvPublic", nvPublic);
    };
    
    
};

//<<<

