package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This sized buffer holds the largest ECC parameter (coordinate) supported by the TPM.  */
public class TPM2B_ECC_PARAMETER extends TpmStructure implements TPMU_SENSITIVE_COMPOSITE
{
    /** The parameter data  */
    public byte[] buffer;
    
    public TPM2B_ECC_PARAMETER() {}
    
    /** @param _buffer The parameter data  */
    public TPM2B_ECC_PARAMETER(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ECC; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_ECC_PARAMETER fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_ECC_PARAMETER.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_ECC_PARAMETER fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_ECC_PARAMETER fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_ECC_PARAMETER.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_ECC_PARAMETER");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "buffer", buffer);
    }
}

//<<<
