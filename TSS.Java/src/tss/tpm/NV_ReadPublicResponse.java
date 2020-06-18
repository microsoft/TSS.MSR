package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to read the public area and Name of an NV Index. The public area
 *  of an Index is not privacy-sensitive and no authorization is required to read this data.
 */
public class NV_ReadPublicResponse extends TpmStructure
{
    /** The public area of the NV Index  */
    public TPMS_NV_PUBLIC nvPublic;
    
    /** The Name of the nvIndex  */
    public byte[] nvName;
    
    public NV_ReadPublicResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeShort(nvPublic != null ? nvPublic.toTpm().length : 0);
        if (nvPublic != null)
            nvPublic.toTpm(buf);
        buf.writeSizedByteBuf(nvName);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _nvPublicSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _nvPublicSize));
        nvPublic = TPMS_NV_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
        int _nvNameSize = buf.readShort() & 0xFFFF;
        nvName = new byte[_nvNameSize];
        buf.readArrayOfInts(nvName, 1, _nvNameSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static NV_ReadPublicResponse fromBytes (byte[] byteBuf) 
    {
        NV_ReadPublicResponse ret = new NV_ReadPublicResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static NV_ReadPublicResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static NV_ReadPublicResponse fromTpm (InByteBuf buf) 
    {
        NV_ReadPublicResponse ret = new NV_ReadPublicResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_ReadPublic_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_NV_PUBLIC", "nvPublic", nvPublic);
        _p.add(d, "byte", "nvName", nvName);
    }
}

//<<<
