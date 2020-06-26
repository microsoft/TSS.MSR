package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure holds two ECC coordinates that, together, make up an ECC point.  */
public class TPMS_ECC_POINT extends TpmStructure implements TPMU_PUBLIC_ID
{
    /** X coordinate  */
    public byte[] x;
    
    /** Y coordinate  */
    public byte[] y;
    
    public TPMS_ECC_POINT() {}
    
    /** @param _x X coordinate
     *  @param _y Y coordinate
     */
    public TPMS_ECC_POINT(byte[] _x, byte[] _y)
    {
        x = _x;
        y = _y;
    }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ECC; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(x);
        buf.writeSizedByteBuf(y);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        x = buf.readSizedByteBuf();
        y = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_ECC_POINT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_ECC_POINT.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_ECC_POINT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_ECC_POINT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_ECC_POINT.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ECC_POINT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "x", x);
        _p.add(d, "byte", "y", y);
    }
}

//<<<
