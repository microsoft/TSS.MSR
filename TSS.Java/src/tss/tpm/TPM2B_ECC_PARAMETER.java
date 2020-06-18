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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(buffer);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readShort() & 0xFFFF;
        buffer = new byte[_size];
        buf.readArrayOfInts(buffer, 1, _size);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2B_ECC_PARAMETER fromBytes (byte[] byteBuf) 
    {
        TPM2B_ECC_PARAMETER ret = new TPM2B_ECC_PARAMETER();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_ECC_PARAMETER fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2B_ECC_PARAMETER fromTpm (InByteBuf buf) 
    {
        TPM2B_ECC_PARAMETER ret = new TPM2B_ECC_PARAMETER();
        ret.initFromTpm(buf);
        return ret;
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
