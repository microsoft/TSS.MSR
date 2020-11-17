package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to load objects into the TPM. This command is used when both a
 *  TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be
 *  loaded, the TPM2_LoadExternal command is used.
 */
public class LoadResponse extends RespStructure
{
    /** Handle of type TPM_HT_TRANSIENT for the loaded object */
    public TPM_HANDLE handle;

    /** Name of the loaded object */
    public byte[] name;

    public LoadResponse() { handle = new TPM_HANDLE(); }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(name); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { name = buf.readSizedByteBuf(); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static LoadResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(LoadResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static LoadResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static LoadResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(LoadResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("LoadResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte[]", "name", name);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public TPM_HANDLE getHandle() { return handle; }

    @Override
    public void setHandle(TPM_HANDLE h) { handle = h; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
