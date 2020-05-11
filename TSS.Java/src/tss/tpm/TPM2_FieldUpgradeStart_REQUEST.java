package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command uses platformPolicy and a TPM Vendor Authorization Key to
 *  authorize a Field Upgrade Manifest.
 */
public class TPM2_FieldUpgradeStart_REQUEST extends TpmStructure
{
    /**
     *  TPM_RH_PLATFORM+{PP}
     *  Auth Index:1
     *  Auth Role: ADMIN
     */
    public TPM_HANDLE authorization;
    
    /**
     *  handle of a public area that contains the TPM Vendor Authorization Key that will be used
     *  to validate manifestSignature
     *  Auth Index: None
     */
    public TPM_HANDLE keyHandle;
    
    /** digest of the first block in the field upgrade sequence */
    public byte[] fuDigest;
    public TPM_ALG_ID manifestSignatureSigAlg() { return manifestSignature != null ? manifestSignature.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** signature over fuDigest using the key associated with keyHandle (not optional) */
    public TPMU_SIGNATURE manifestSignature;
    
    public TPM2_FieldUpgradeStart_REQUEST()
    {
        authorization = new TPM_HANDLE();
        keyHandle = new TPM_HANDLE();
    }

    /**
     *  @param _authorization TPM_RH_PLATFORM+{PP}
     *         Auth Index:1
     *         Auth Role: ADMIN
     *  @param _keyHandle handle of a public area that contains the TPM Vendor Authorization Key that will be used
     *         to validate manifestSignature
     *         Auth Index: None
     *  @param _fuDigest digest of the first block in the field upgrade sequence
     *  @param _manifestSignature signature over fuDigest using the key associated with keyHandle (not optional)
     *         (One of [TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *         TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE])
     */
    public TPM2_FieldUpgradeStart_REQUEST(TPM_HANDLE _authorization, TPM_HANDLE _keyHandle, byte[] _fuDigest, TPMU_SIGNATURE _manifestSignature)
    {
        authorization = _authorization;
        keyHandle = _keyHandle;
        fuDigest = _fuDigest;
        manifestSignature = _manifestSignature;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authorization.toTpm(buf);
        keyHandle.toTpm(buf);
        buf.writeSizedByteBuf(fuDigest);
        manifestSignature.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)manifestSignature).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authorization = TPM_HANDLE.fromTpm(buf);
        keyHandle = TPM_HANDLE.fromTpm(buf);
        int _fuDigestSize = buf.readShort() & 0xFFFF;
        fuDigest = new byte[_fuDigestSize];
        buf.readArrayOfInts(fuDigest, 1, _fuDigestSize);
        int _manifestSignatureSigAlg = buf.readShort() & 0xFFFF;
        manifestSignature = UnionFactory.create("TPMU_SIGNATURE", new TPM_ALG_ID(_manifestSignatureSigAlg));
        manifestSignature.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_FieldUpgradeStart_REQUEST fromTpm (byte[] x) 
    {
        TPM2_FieldUpgradeStart_REQUEST ret = new TPM2_FieldUpgradeStart_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_FieldUpgradeStart_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_FieldUpgradeStart_REQUEST ret = new TPM2_FieldUpgradeStart_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FieldUpgradeStart_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authorization", authorization);
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte", "fuDigest", fuDigest);
        _p.add(d, "TPMU_SIGNATURE", "manifestSignature", manifestSignature);
    }
}

//<<<
