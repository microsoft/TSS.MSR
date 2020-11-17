package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the attested data for TPM2_Certify(). */
public class TPMS_CERTIFY_INFO extends TpmStructure implements TPMU_ATTEST
{
    /** Name of the certified object */
    public byte[] name;

    /** Qualified Name of the certified object */
    public byte[] qualifiedName;

    public TPMS_CERTIFY_INFO() {}

    /** @param _name Name of the certified object
     *  @param _qualifiedName Qualified Name of the certified object
     */
    public TPMS_CERTIFY_INFO(byte[] _name, byte[] _qualifiedName)
    {
        name = _name;
        qualifiedName = _qualifiedName;
    }

    /** TpmUnion method */
    public TPM_ST GetUnionSelector() { return TPM_ST.ATTEST_CERTIFY; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(name);
        buf.writeSizedByteBuf(qualifiedName);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        name = buf.readSizedByteBuf();
        qualifiedName = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_CERTIFY_INFO fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_CERTIFY_INFO.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_CERTIFY_INFO fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_CERTIFY_INFO fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_CERTIFY_INFO.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CERTIFY_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "name", name);
        _p.add(d, "byte[]", "qualifiedName", qualifiedName);
    }
}

//<<<
