package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to read the public area and Name of an NV Index. The public area
 *  of an Index is not privacy-sensitive and no authorization is required to read this data.
 */
public class NV_ReadPublicResponse extends RespStructure
{
    /** The public area of the NV Index */
    public TPMS_NV_PUBLIC nvPublic;

    /** The Name of the nvIndex */
    public byte[] nvName;

    public NV_ReadPublicResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(nvPublic);
        buf.writeSizedByteBuf(nvName);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        nvPublic = buf.createSizedObj(TPMS_NV_PUBLIC.class);
        nvName = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static NV_ReadPublicResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(NV_ReadPublicResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static NV_ReadPublicResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static NV_ReadPublicResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(NV_ReadPublicResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("NV_ReadPublicResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_NV_PUBLIC", "nvPublic", nvPublic);
        _p.add(d, "byte[]", "nvName", nvName);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
