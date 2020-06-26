package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is defined to allow a point to be a single sized parameter so that it
 *  may be encrypted.
 */
public class TPM2B_ECC_POINT extends TpmStructure
{
    /** Coordinates  */
    public TPMS_ECC_POINT point;
    
    public TPM2B_ECC_POINT() {}
    
    /** @param _point Coordinates  */
    public TPM2B_ECC_POINT(TPMS_ECC_POINT _point) { point = _point; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(point); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { point = buf.createSizedObj(TPMS_ECC_POINT.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_ECC_POINT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_ECC_POINT.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_ECC_POINT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_ECC_POINT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_ECC_POINT.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_ECC_POINT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "point", point);
    }
}

//<<<
