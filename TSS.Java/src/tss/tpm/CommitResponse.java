package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** TPM2_Commit() performs the first part of an ECC anonymous signing operation. The TPM
 *  will perform the point multiplications on the provided points and return intermediate
 *  signing values. The signHandle parameter shall refer to an ECC key and the signing
 *  scheme must be anonymous (TPM_RC_SCHEME).
 */
public class CommitResponse extends TpmStructure
{
    /** ECC point K [ds](x2, y2)  */
    public TPMS_ECC_POINT K;
    
    /** ECC point L [r](x2, y2)  */
    public TPMS_ECC_POINT L;
    
    /** ECC point E [r]P1  */
    public TPMS_ECC_POINT E;
    
    /** Least-significant 16 bits of commitCount  */
    public short counter;
    
    public CommitResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeShort(K != null ? K.toTpm().length : 0);
        if (K != null)
            K.toTpm(buf);
        buf.writeShort(L != null ? L.toTpm().length : 0);
        if (L != null)
            L.toTpm(buf);
        buf.writeShort(E != null ? E.toTpm().length : 0);
        if (E != null)
            E.toTpm(buf);
        buf.writeShort(counter);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _KSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _KSize));
        K = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        int _LSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _LSize));
        L = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        int _ESize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _ESize));
        E = TPMS_ECC_POINT.fromTpm(buf);
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
    
    public static CommitResponse fromBytes (byte[] byteBuf) 
    {
        CommitResponse ret = new CommitResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static CommitResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static CommitResponse fromTpm (InByteBuf buf) 
    {
        CommitResponse ret = new CommitResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Commit_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "K", K);
        _p.add(d, "TPMS_ECC_POINT", "L", L);
        _p.add(d, "TPMS_ECC_POINT", "E", E);
        _p.add(d, "short", "counter", counter);
    }
}

//<<<
