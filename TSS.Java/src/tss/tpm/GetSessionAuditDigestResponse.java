package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns a digital signature of the audit session digest. */
public class GetSessionAuditDigestResponse extends TpmStructure
{
    /** the audit information that was signed */
    public TPMS_ATTEST auditInfo;
    
    /** the signature over auditInfo */
    public TPMU_SIGNATURE signature;
    
    public GetSessionAuditDigestResponse() {}
    public int GetUnionSelector_signature()
    {
        if (signature instanceof TPMS_SIGNATURE_RSASSA) { return 0x0014; }
        if (signature instanceof TPMS_SIGNATURE_RSAPSS) { return 0x0016; }
        if (signature instanceof TPMS_SIGNATURE_ECDSA) { return 0x0018; }
        if (signature instanceof TPMS_SIGNATURE_ECDAA) { return 0x001A; }
        if (signature instanceof TPMS_SIGNATURE_SM2) { return 0x001B; }
        if (signature instanceof TPMS_SIGNATURE_ECSCHNORR) { return 0x001C; }
        if (signature instanceof TPMT_HA) { return 0x0005; }
        if (signature instanceof TPMS_SCHEME_HASH) { return 0x7FFF; }
        if (signature instanceof TPMS_NULL_SIGNATURE) { return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeShort(auditInfo != null ? auditInfo.toTpm().length : 0);
        if (auditInfo != null)
            auditInfo.toTpm(buf);
        buf.writeShort(GetUnionSelector_signature());
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
        signature = null;
        if (_signatureSigAlg == TPM_ALG_ID.RSASSA.toInt()) { signature = new TPMS_SIGNATURE_RSASSA(); }
        else if (_signatureSigAlg == TPM_ALG_ID.RSAPSS.toInt()) { signature = new TPMS_SIGNATURE_RSAPSS(); }
        else if (_signatureSigAlg == TPM_ALG_ID.ECDSA.toInt()) { signature = new TPMS_SIGNATURE_ECDSA(); }
        else if (_signatureSigAlg == TPM_ALG_ID.ECDAA.toInt()) { signature = new TPMS_SIGNATURE_ECDAA(); }
        // code generator workaround BUGBUG >> (probChild)else if (_signatureSigAlg == TPM_ALG_ID.SM2.toInt()) { signature = new TPMS_SIGNATURE_SM2(); }
        // code generator workaround BUGBUG >> (probChild)else if (_signatureSigAlg == TPM_ALG_ID.ECSCHNORR.toInt()) { signature = new TPMS_SIGNATURE_ECSCHNORR(); }
        else if (_signatureSigAlg == TPM_ALG_ID.HMAC.toInt()) { signature = new TPMT_HA(); }
        else if (_signatureSigAlg == TPM_ALG_ID.ANY.toInt()) { signature = new TPMS_SCHEME_HASH(); }
        else if (_signatureSigAlg == TPM_ALG_ID.NULL.toInt()) { signature = new TPMS_NULL_SIGNATURE(); }
        if (signature == null) throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_signatureSigAlg).name());
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

