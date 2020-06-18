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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(integrity);
        buf.writeByteBuf(encrypted);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _integritySize = buf.readShort() & 0xFFFF;
        integrity = new byte[_integritySize];
        buf.readArrayOfInts(integrity, 1, _integritySize);
        InByteBuf.SizedStructInfo si = buf.structSize.peek();
        int _encryptedSize = si.Size - (buf.curPos() - si.StartPos);
        encrypted = new byte[_encryptedSize];
        buf.readArrayOfInts(encrypted, 1, _encryptedSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMS_CONTEXT_DATA fromBytes (byte[] byteBuf) 
    {
        TPMS_CONTEXT_DATA ret = new TPMS_CONTEXT_DATA();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_CONTEXT_DATA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPMS_CONTEXT_DATA fromTpm (InByteBuf buf) 
    {
        TPMS_CONTEXT_DATA ret = new TPMS_CONTEXT_DATA();
        ret.initFromTpm(buf);
        return ret;
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
        _p.add(d, "byte", "integrity", integrity);
        _p.add(d, "byte", "encrypted", encrypted);
    }
}

//<<<
