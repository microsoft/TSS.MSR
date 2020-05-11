package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command returns the current value of the command audit digest, a digest of the
 *  commands being audited, and the audit hash algorithm. These values are placed in an
 *  attestation structure and signed with the key referenced by signHandle.
 */
public class TPM2_GetCommandAuditDigest_REQUEST extends TpmStructure
{
    /**
     *  handle of the privacy administrator (TPM_RH_ENDORSEMENT)
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE privacyHandle;
    
    /**
     *  the handle of the signing key
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;
    
    /** other data to associate with this audit digest */
    public byte[] qualifyingData;
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** signing scheme to use if the scheme for signHandle is TPM_ALG_NULL */
    public TPMU_SIG_SCHEME inScheme;
    
    public TPM2_GetCommandAuditDigest_REQUEST()
    {
        privacyHandle = new TPM_HANDLE();
        signHandle = new TPM_HANDLE();
    }

    /**
     *  @param _privacyHandle handle of the privacy administrator (TPM_RH_ENDORSEMENT)
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _signHandle the handle of the signing key
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _qualifyingData other data to associate with this audit digest
     *  @param _inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         (One of [TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME])
     */
    public TPM2_GetCommandAuditDigest_REQUEST(TPM_HANDLE _privacyHandle, TPM_HANDLE _signHandle, byte[] _qualifyingData, TPMU_SIG_SCHEME _inScheme)
    {
        privacyHandle = _privacyHandle;
        signHandle = _signHandle;
        qualifyingData = _qualifyingData;
        inScheme = _inScheme;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        privacyHandle.toTpm(buf);
        signHandle.toTpm(buf);
        buf.writeSizedByteBuf(qualifyingData);
        inScheme.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)inScheme).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        privacyHandle = TPM_HANDLE.fromTpm(buf);
        signHandle = TPM_HANDLE.fromTpm(buf);
        int _qualifyingDataSize = buf.readShort() & 0xFFFF;
        qualifyingData = new byte[_qualifyingDataSize];
        buf.readArrayOfInts(qualifyingData, 1, _qualifyingDataSize);
        int _inSchemeScheme = buf.readShort() & 0xFFFF;
        inScheme = UnionFactory.create("TPMU_SIG_SCHEME", new TPM_ALG_ID(_inSchemeScheme));
        inScheme.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_GetCommandAuditDigest_REQUEST fromTpm (byte[] x) 
    {
        TPM2_GetCommandAuditDigest_REQUEST ret = new TPM2_GetCommandAuditDigest_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_GetCommandAuditDigest_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_GetCommandAuditDigest_REQUEST ret = new TPM2_GetCommandAuditDigest_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetCommandAuditDigest_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "privacyHandle", privacyHandle);
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "byte", "qualifyingData", qualifyingData);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
    }
}

//<<<
