package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Contains a PCR index and associated hash(pcr-value) [TSS]  */
public class PcrValue extends TpmStructure
{
    /** PCR Index  */
    public int index;

    /** PCR Value  */
    public TPMT_HA value;

    public PcrValue() {}

    /** @param _index PCR Index
     *  @param _value PCR Value
     */
    public PcrValue(int _index, TPMT_HA _value)
    {
        index = _index;
        value = _value;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeInt(index);
        value.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        index = buf.readInt();
        value = TPMT_HA.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static PcrValue fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(PcrValue.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static PcrValue fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static PcrValue fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(PcrValue.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("PcrValue");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "int", "index", index);
        _p.add(d, "TPMT_HA", "value", value);
    }
}

//<<<
