package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 192 Definition of TPM2B_ENCRYPTED_SECRET Structure  */
public class TPM2B_ENCRYPTED_SECRET extends TpmStructure
{
    /** Secret  */
    public byte[] secret;
    
    public TPM2B_ENCRYPTED_SECRET() {}
    
    /** @param _secret Secret  */
    public TPM2B_ENCRYPTED_SECRET(byte[] _secret) { secret = _secret; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(secret);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readShort() & 0xFFFF;
        secret = new byte[_size];
        buf.readArrayOfInts(secret, 1, _size);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2B_ENCRYPTED_SECRET fromBytes (byte[] byteBuf) 
    {
        TPM2B_ENCRYPTED_SECRET ret = new TPM2B_ENCRYPTED_SECRET();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_ENCRYPTED_SECRET fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2B_ENCRYPTED_SECRET fromTpm (InByteBuf buf) 
    {
        TPM2B_ENCRYPTED_SECRET ret = new TPM2B_ENCRYPTED_SECRET();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_ENCRYPTED_SECRET");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "secret", secret);
    }
}

//<<<
