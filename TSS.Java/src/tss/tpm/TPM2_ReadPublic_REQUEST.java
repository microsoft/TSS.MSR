package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows access to the public area of a loaded object. */
public class TPM2_ReadPublic_REQUEST extends ReqStructure
{
    /** TPM handle of an object
     *  Auth Index: None
     */
    public TPM_HANDLE objectHandle;

    public TPM2_ReadPublic_REQUEST() { objectHandle = new TPM_HANDLE(); }

    /** @param _objectHandle TPM handle of an object
     *         Auth Index: None
     */
    public TPM2_ReadPublic_REQUEST(TPM_HANDLE _objectHandle) { objectHandle = _objectHandle; }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ReadPublic_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ReadPublic_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ReadPublic_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ReadPublic_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ReadPublic_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ReadPublic_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {objectHandle}; }
}

//<<<
