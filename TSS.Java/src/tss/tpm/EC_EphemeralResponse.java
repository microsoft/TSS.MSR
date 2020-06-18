package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.  */
public class EC_EphemeralResponse extends TpmStructure
{
    /** Ephemeral public key Q [r]G  */
    public TPMS_ECC_POINT Q;
    
    /** Least-significant 16 bits of commitCount  */
    public short counter;
    
    public EC_EphemeralResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeShort(Q != null ? Q.toTpm().length : 0);
        if (Q != null)
            Q.toTpm(buf);
        buf.writeShort(counter);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _QSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _QSize));
        Q = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        counter = buf.readShort();
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static EC_EphemeralResponse fromBytes (byte[] byteBuf) 
    {
        EC_EphemeralResponse ret = new EC_EphemeralResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static EC_EphemeralResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static EC_EphemeralResponse fromTpm (InByteBuf buf) 
    {
        EC_EphemeralResponse ret = new EC_EphemeralResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EC_Ephemeral_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "Q", Q);
        _p.add(d, "short", "counter", counter);
    }
}

//<<<
