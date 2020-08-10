package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure holds the integrity value and the encrypted data for a context.  */
public class TPMS_CONTEXT_DATA extends TpmStructure
{
    /** The integrity value  */
    public byte[] integrity;
    
    /** The sensitive area  */
    public byte[] encrypted;
    
    public TPMS_CONTEXT_DATA() {}
    
    /** @param _integrity The integrity value
     *  @param _encrypted The sensitive area
     */
    public TPMS_CONTEXT_DATA(byte[] _integrity, byte[] _encrypted)
    {
        integrity = _integrity;
        encrypted = _encrypted;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(integrity);
        buf.writeByteBuf(encrypted);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        integrity = buf.readSizedByteBuf();
        encrypted = buf.readByteBuf(buf.getCurStuctRemainingSize());
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_CONTEXT_DATA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_CONTEXT_DATA.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_CONTEXT_DATA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_CONTEXT_DATA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_CONTEXT_DATA.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CONTEXT_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "integrity", integrity);
        _p.add(d, "byte[]", "encrypted", encrypted);
    }
}

//<<<
