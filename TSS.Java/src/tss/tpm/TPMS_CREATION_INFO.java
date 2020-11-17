package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the attested data for TPM2_CertifyCreation(). */
public class TPMS_CREATION_INFO extends TpmStructure implements TPMU_ATTEST
{
    /** Name of the object */
    public byte[] objectName;

    /** CreationHash */
    public byte[] creationHash;

    public TPMS_CREATION_INFO() {}

    /** @param _objectName Name of the object
     *  @param _creationHash CreationHash
     */
    public TPMS_CREATION_INFO(byte[] _objectName, byte[] _creationHash)
    {
        objectName = _objectName;
        creationHash = _creationHash;
    }

    /** TpmUnion method */
    public TPM_ST GetUnionSelector() { return TPM_ST.ATTEST_CREATION; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(objectName);
        buf.writeSizedByteBuf(creationHash);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        objectName = buf.readSizedByteBuf();
        creationHash = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_CREATION_INFO fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_CREATION_INFO.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_CREATION_INFO fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_CREATION_INFO fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_CREATION_INFO.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CREATION_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "objectName", objectName);
        _p.add(d, "byte[]", "creationHash", creationHash);
    }
}

//<<<
