package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This buffer holds a Name for any entity type.  */
public class TPM2B_NAME extends TpmStructure
{
    /** The Name structure  */
    public byte[] name;
    
    public TPM2B_NAME() {}
    
    /** @param _name The Name structure  */
    public TPM2B_NAME(byte[] _name) { name = _name; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(name); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { name = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_NAME fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_NAME.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_NAME fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_NAME fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_NAME.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_NAME");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "name", name);
    }
}

//<<<
