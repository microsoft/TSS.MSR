package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  The purpose of this command is to certify the contents of an NV Index or
 *  portion of an NV Index.
 */
public class NV_CertifyResponse extends TpmStructure
{
    /** the structure that was signed */
    public TPMS_ATTEST certifyInfo;
    
    /** the asymmetric signature over certifyInfo using the key referenced by signHandle */
    public TPMU_SIGNATURE signature;
    
    public NV_CertifyResponse() {}
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
        buf.writeShort(certifyInfo != null ? certifyInfo.toTpm().length : 0);
        if (certifyInfo != null)
            certifyInfo.toTpm(buf);
        buf.writeShort(GetUnionSelector_signature());
        ((TpmMarshaller)signature).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _certifyInfoSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _certifyInfoSize));
        certifyInfo = TPMS_ATTEST.fromTpm(buf);
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

    public static NV_CertifyResponse fromTpm (byte[] x) 
    {
        NV_CertifyResponse ret = new NV_CertifyResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static NV_CertifyResponse fromTpm (InByteBuf buf) 
    {
        NV_CertifyResponse ret = new NV_CertifyResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_Certify_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ATTEST", "certifyInfo", certifyInfo);
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    }
}

//<<<

