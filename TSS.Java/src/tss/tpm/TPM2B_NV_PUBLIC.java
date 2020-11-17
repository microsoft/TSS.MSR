package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used when a TPMS_NV_PUBLIC is sent on the TPM interface. */
public class TPM2B_NV_PUBLIC extends TpmStructure
{
    /** The public area */
    public TPMS_NV_PUBLIC nvPublic;

    public TPM2B_NV_PUBLIC() {}

    /** @param _nvPublic The public area */
    public TPM2B_NV_PUBLIC(TPMS_NV_PUBLIC _nvPublic) { nvPublic = _nvPublic; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(nvPublic); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { nvPublic = buf.createSizedObj(TPMS_NV_PUBLIC.class); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_NV_PUBLIC fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_NV_PUBLIC.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_NV_PUBLIC fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_NV_PUBLIC fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_NV_PUBLIC.class);
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
    }
}

//<<<
