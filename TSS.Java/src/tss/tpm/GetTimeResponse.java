package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the current values of Time and Clock. */
public class GetTimeResponse extends TpmStructure
{
    /** standard TPM-generated attestation block */
    public TPMS_ATTEST timeInfo;
    public TPM_ALG_ID signatureSigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** the signature over timeInfo */
    public TPMU_SIGNATURE signature;
    
    public GetTimeResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeShort(timeInfo != null ? timeInfo.toTpm().length : 0);
        if (timeInfo != null)
            timeInfo.toTpm(buf);
        signature.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)signature).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _timeInfoSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _timeInfoSize));
        timeInfo = TPMS_ATTEST.fromTpm(buf);
        buf.structSize.pop();
        int _signatureSigAlg = buf.readShort() & 0xFFFF;
        signature = UnionFactory.create("TPMU_SIGNATURE", new TPM_ALG_ID(_signatureSigAlg));
        signature.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static GetTimeResponse fromTpm (byte[] x) 
    {
        GetTimeResponse ret = new GetTimeResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static GetTimeResponse fromTpm (InByteBuf buf) 
    {
        GetTimeResponse ret = new GetTimeResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetTime_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ATTEST", "timeInfo", timeInfo);
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    }
}

//<<<
