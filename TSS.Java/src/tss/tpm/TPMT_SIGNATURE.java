package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  Table 190 shows the basic algorithm-agile structure when a symmetric or asymmetric
 *  signature is indicated. The sigAlg parameter indicates the algorithm used for the
 *  signature. This structure is output from commands such as the attestation commands and
 *  TPM2_Sign, and is an input to commands such as TPM2_VerifySignature(),
 *  TPM2_PolicySigned(), and TPM2_FieldUpgradeStart().
 */
public class TPMT_SIGNATURE extends TpmStructure
{
    /** selector of the algorithm used to construct the signature */
    public TPM_ALG_ID sigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** This shall be the actual signature information. */
    public TPMU_SIGNATURE signature;
    
    public TPMT_SIGNATURE() {}
    
    /**
     *  @param _signature This shall be the actual signature information.
     *         (One of [TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *         TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE])
     */
    public TPMT_SIGNATURE(TPMU_SIGNATURE _signature) { signature = _signature; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        if (signature == null) return;
        signature.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)signature).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _sigAlg = buf.readShort() & 0xFFFF;
        signature = UnionFactory.create("TPMU_SIGNATURE", new TPM_ALG_ID(_sigAlg));
        signature.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPMT_SIGNATURE fromTpm (byte[] x) 
    {
        TPMT_SIGNATURE ret = new TPMT_SIGNATURE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMT_SIGNATURE fromTpm (InByteBuf buf) 
    {
        TPMT_SIGNATURE ret = new TPMT_SIGNATURE();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SIGNATURE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    }
}

//<<<
