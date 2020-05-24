package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the current values of Time and Clock. */
public class TPM2_GetTime_REQUEST extends TpmStructure
{
    /**
     *  handle of the privacy administrator (TPM_RH_ENDORSEMENT)
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE privacyAdminHandle;
    
    /**
     *  the keyHandle identifier of a loaded key that can perform digital signatures
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;
    
    /** data to tick stamp */
    public byte[] qualifyingData;
    
    /** scheme selector */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** signing scheme to use if the scheme for signHandle is TPM_ALG_NULL */
    public TPMU_SIG_SCHEME inScheme;
    
    public TPM2_GetTime_REQUEST()
    {
        privacyAdminHandle = new TPM_HANDLE();
        signHandle = new TPM_HANDLE();
    }

    /**
     *  @param _privacyAdminHandle handle of the privacy administrator (TPM_RH_ENDORSEMENT)
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _signHandle the keyHandle identifier of a loaded key that can perform digital signatures
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _qualifyingData data to tick stamp
     *  @param _inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         (One of [TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME])
     */
    public TPM2_GetTime_REQUEST(TPM_HANDLE _privacyAdminHandle, TPM_HANDLE _signHandle, byte[] _qualifyingData, TPMU_SIG_SCHEME _inScheme)
    {
        privacyAdminHandle = _privacyAdminHandle;
        signHandle = _signHandle;
        qualifyingData = _qualifyingData;
        inScheme = _inScheme;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(qualifyingData);
        inScheme.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)inScheme).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
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
        return buf.buffer();
    }

    public static TPM2_GetTime_REQUEST fromTpm (byte[] x) 
    {
        TPM2_GetTime_REQUEST ret = new TPM2_GetTime_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_GetTime_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_GetTime_REQUEST ret = new TPM2_GetTime_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetTime_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "privacyAdminHandle", privacyAdminHandle);
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "byte", "qualifyingData", qualifyingData);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
    }
}

//<<<
