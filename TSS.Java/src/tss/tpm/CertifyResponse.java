package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  The purpose of this command is to prove that an object with a specific Name is loaded in
 *  the TPM. By certifying that the object is loaded, the TPM warrants that a public area with
 *  a given Name is self-consistent and associated with a valid sensitive area. If a relying
 *  party has a public area that has the same Name as a Name certified with this command, then the
 *  values in that public area are correct.
 */
public class CertifyResponse extends TpmStructure
{
    /** the structure that was signed */
    public TPMS_ATTEST certifyInfo;
    public TPM_ALG_ID signatureSigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** the asymmetric signature over certifyInfo using the key referenced by signHandle */
    public TPMU_SIGNATURE signature;
    
    public CertifyResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeShort(certifyInfo != null ? certifyInfo.toTpm().length : 0);
        if (certifyInfo != null)
            certifyInfo.toTpm(buf);
        signature.GetUnionSelector().toTpm(buf);
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

    public static CertifyResponse fromTpm (byte[] x) 
    {
        CertifyResponse ret = new CertifyResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static CertifyResponse fromTpm (InByteBuf buf) 
    {
        CertifyResponse ret = new CertifyResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Certify_RESPONSE");
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
