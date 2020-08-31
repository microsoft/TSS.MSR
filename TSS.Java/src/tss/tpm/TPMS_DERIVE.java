package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure contains the label and context fields for a derived object. These
 *  values are used in the derivation KDF. The values in the unique field of inPublic area
 *  template take precedence over the values in the inSensitive parameter.
 */
public class TPMS_DERIVE extends TpmStructure implements TPMU_SENSITIVE_CREATE, TPMU_PUBLIC_ID
{
    public byte[] label;
    public byte[] context;

    public TPMS_DERIVE() {}

    /** @param _label TBD
     *  @param _context TBD
     */
    public TPMS_DERIVE(byte[] _label, byte[] _context)
    {
        label = _label;
        context = _context;
    }

    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ANY2; }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(label);
        buf.writeSizedByteBuf(context);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        label = buf.readSizedByteBuf();
        context = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_DERIVE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_DERIVE.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_DERIVE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_DERIVE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_DERIVE.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_DERIVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "label", label);
        _p.add(d, "byte[]", "context", context);
    }
}

//<<<
