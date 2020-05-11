package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns a digital signature of the audit session digest. */
public class GetSessionAuditDigestResponse extends TpmStructure
{
    /** the audit information that was signed */
    public TPMS_ATTEST auditInfo;
    public TPM_ALG_ID signatureSigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** the signature over auditInfo */
    public TPMU_SIGNATURE signature;
    
    public GetSessionAuditDigestResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeShort(auditInfo != null ? auditInfo.toTpm().length : 0);
        if (auditInfo != null)
            auditInfo.toTpm(buf);
        signature.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)signature).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _auditInfoSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _auditInfoSize));
        auditInfo = TPMS_ATTEST.fromTpm(buf);
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

    public static GetSessionAuditDigestResponse fromTpm (byte[] x) 
    {
        GetSessionAuditDigestResponse ret = new GetSessionAuditDigestResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static GetSessionAuditDigestResponse fromTpm (InByteBuf buf) 
    {
        GetSessionAuditDigestResponse ret = new GetSessionAuditDigestResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetSessionAuditDigest_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ATTEST", "auditInfo", auditInfo);
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    }
}

//<<<
