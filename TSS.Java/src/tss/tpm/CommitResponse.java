package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* TPM2_Commit() performs the first part of an ECC anonymous signing operation. The TPM will perform the point multiplications on the provided points and return intermediate signing values. The signHandle parameter shall refer to an ECC key and the signing scheme must be anonymous (TPM_RC_SCHEME).
*/
public class CommitResponse extends TpmStructure
{
    /**
     * TPM2_Commit() performs the first part of an ECC anonymous signing operation. The TPM will perform the point multiplications on the provided points and return intermediate signing values. The signHandle parameter shall refer to an ECC key and the signing scheme must be anonymous (TPM_RC_SCHEME).
     * 
     * @param _K ECC point K [ds](x2, y2) 
     * @param _L ECC point L [r](x2, y2) 
     * @param _E ECC point E [r]P1 
     * @param _counter least-significant 16 bits of commitCount
     */
    public CommitResponse(TPMS_ECC_POINT _K,TPMS_ECC_POINT _L,TPMS_ECC_POINT _E,int _counter)
    {
        K = _K;
        L = _L;
        E = _E;
        counter = (short)_counter;
    }
    /**
    * TPM2_Commit() performs the first part of an ECC anonymous signing operation. The TPM will perform the point multiplications on the provided points and return intermediate signing values. The signHandle parameter shall refer to an ECC key and the signing scheme must be anonymous (TPM_RC_SCHEME).
    */
    public CommitResponse() {};
    /**
    * size of the remainder of this structure
    */
    // private short KSize;
    /**
    * ECC point K [ds](x2, y2)
    */
    public TPMS_ECC_POINT K;
    /**
    * size of the remainder of this structure
    */
    // private short LSize;
    /**
    * ECC point L [r](x2, y2)
    */
    public TPMS_ECC_POINT L;
    /**
    * size of the remainder of this structure
    */
    // private short ESize;
    /**
    * ECC point E [r]P1
    */
    public TPMS_ECC_POINT E;
    /**
    * least-significant 16 bits of commitCount
    */
    public short counter;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((K!=null)?K.toTpm().length:0, 2);
        if(K!=null)
            K.toTpm(buf);
        buf.writeInt((L!=null)?L.toTpm().length:0, 2);
        if(L!=null)
            L.toTpm(buf);
        buf.writeInt((E!=null)?E.toTpm().length:0, 2);
        if(E!=null)
            E.toTpm(buf);
        buf.write(counter);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _KSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _KSize));
        K = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        int _LSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _LSize));
        L = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        int _ESize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _ESize));
        E = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        counter = (short) buf.readInt(2);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static CommitResponse fromTpm (byte[] x) 
    {
        CommitResponse ret = new CommitResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
        _p.add(d, "ushort", "counter", counter);
    };
    
    
};

//<<<

