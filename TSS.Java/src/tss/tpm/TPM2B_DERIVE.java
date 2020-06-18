package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 147 Definition of TPM2B_DERIVE Structure  */
public class TPM2B_DERIVE extends TpmStructure
{
    /** Symmetric data for a created object or the label and context for a derived object  */
    public TPMS_DERIVE buffer;
    
    public TPM2B_DERIVE() {}
    
    /** @param _buffer Symmetric data for a created object or the label and context for a
     *  derived object
     */
    public TPM2B_DERIVE(TPMS_DERIVE _buffer) { buffer = _buffer; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeShort(buffer != null ? buffer.toTpm().length : 0);
        if (buffer != null)
            buffer.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _size));
        buffer = TPMS_DERIVE.fromTpm(buf);
        buf.structSize.pop();
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2B_DERIVE fromBytes (byte[] byteBuf) 
    {
        TPM2B_DERIVE ret = new TPM2B_DERIVE();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_DERIVE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2B_DERIVE fromTpm (InByteBuf buf) 
    {
        TPM2B_DERIVE ret = new TPM2B_DERIVE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_DERIVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_DERIVE", "buffer", buffer);
    }
}

//<<<
