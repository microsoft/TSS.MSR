package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** TPM2_Commit() performs the first part of an ECC anonymous signing operation. The TPM
 *  will perform the point multiplications on the provided points and return intermediate
 *  signing values. The signHandle parameter shall refer to an ECC key and the signing
 *  scheme must be anonymous (TPM_RC_SCHEME).
 */
public class CommitResponse extends RespStructure
{
    /** ECC point K [ds](x2, y2)  */
    public TPMS_ECC_POINT K;
    
    /** ECC point L [r](x2, y2)  */
    public TPMS_ECC_POINT L;
    
    /** ECC point E [r]P1  */
    public TPMS_ECC_POINT E;
    
    /** Least-significant 16 bits of commitCount  */
    public int counter;
    
    public CommitResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(K);
        buf.writeSizedObj(L);
        buf.writeSizedObj(E);
        buf.writeShort(counter);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        K = buf.createSizedObj(TPMS_ECC_POINT.class);
        L = buf.createSizedObj(TPMS_ECC_POINT.class);
        E = buf.createSizedObj(TPMS_ECC_POINT.class);
        counter = buf.readShort();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static CommitResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(CommitResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static CommitResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static CommitResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(CommitResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("CommitResponse");
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
        _p.add(d, "int", "counter", counter);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
