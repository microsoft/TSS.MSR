package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This structure is used on each TPM-generated signed structure. The
 *  signature is over this structure.
 */
public class TPMS_ATTEST extends TpmStructure
{
    /** the indication that this structure was created by a TPM (always TPM_GENERATED_VALUE) */
    public TPM_GENERATED magic;
    
    /** type of the attestation structure */
    public TPM_ST type() { return attested.GetUnionSelector(); }
    
    /** Qualified Name of the signing key */
    public byte[] qualifiedSigner;
    
    /**
     *  external information supplied by caller
     *  NOTE A TPM2B_DATA structure provides room for a digest and a method indicator to indicate
     *  the components of the digest. The definition of this method indicator is outside the
     *  scope of this specification.
     */
    public byte[] extraData;
    
    /** Clock, resetCount, restartCount, and Safe */
    public TPMS_CLOCK_INFO clockInfo;
    
    /** TPM-vendor-specific value identifying the version number of the firmware */
    public long firmwareVersion;
    
    /** the type-specific attestation information */
    public TPMU_ATTEST attested;
    
    public TPMS_ATTEST() {}
    
    /**
     *  @param _magic the indication that this structure was created by a TPM (always TPM_GENERATED_VALUE)
     *  @param _qualifiedSigner Qualified Name of the signing key
     *  @param _extraData external information supplied by caller
     *         NOTE A TPM2B_DATA structure provides room for a digest and a method indicator to indicate
     *         the components of the digest. The definition of this method indicator is outside the
     *         scope of this specification.
     *  @param _clockInfo Clock, resetCount, restartCount, and Safe
     *  @param _firmwareVersion TPM-vendor-specific value identifying the version number of the firmware
     *  @param _attested the type-specific attestation information
     *         (One of [TPMS_CERTIFY_INFO, TPMS_CREATION_INFO, TPMS_QUOTE_INFO, TPMS_COMMAND_AUDIT_INFO,
     *         TPMS_SESSION_AUDIT_INFO, TPMS_TIME_ATTEST_INFO, TPMS_NV_CERTIFY_INFO,
     *         TPMS_NV_DIGEST_CERTIFY_INFO])
     */
    public TPMS_ATTEST(TPM_GENERATED _magic, byte[] _qualifiedSigner, byte[] _extraData, TPMS_CLOCK_INFO _clockInfo, long _firmwareVersion, TPMU_ATTEST _attested)
    {
        magic = _magic;
        qualifiedSigner = _qualifiedSigner;
        extraData = _extraData;
        clockInfo = _clockInfo;
        firmwareVersion = _firmwareVersion;
        attested = _attested;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        magic.toTpm(buf);
        attested.GetUnionSelector().toTpm(buf);
        buf.writeSizedByteBuf(qualifiedSigner);
        buf.writeSizedByteBuf(extraData);
        clockInfo.toTpm(buf);
        buf.writeInt64(firmwareVersion);
        ((TpmMarshaller)attested).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        magic = TPM_GENERATED.fromTpm(buf);
        int _type = buf.readShort() & 0xFFFF;
        int _qualifiedSignerSize = buf.readShort() & 0xFFFF;
        qualifiedSigner = new byte[_qualifiedSignerSize];
        buf.readArrayOfInts(qualifiedSigner, 1, _qualifiedSignerSize);
        int _extraDataSize = buf.readShort() & 0xFFFF;
        extraData = new byte[_extraDataSize];
        buf.readArrayOfInts(extraData, 1, _extraDataSize);
        clockInfo = TPMS_CLOCK_INFO.fromTpm(buf);
        firmwareVersion = buf.readInt64();
        attested = UnionFactory.create("TPMU_ATTEST", new TPM_ST(_type));
        attested.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPMS_ATTEST fromTpm (byte[] x) 
    {
        TPMS_ATTEST ret = new TPMS_ATTEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_ATTEST fromTpm (InByteBuf buf) 
    {
        TPMS_ATTEST ret = new TPMS_ATTEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ATTEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_GENERATED", "magic", magic);
        _p.add(d, "byte", "qualifiedSigner", qualifiedSigner);
        _p.add(d, "byte", "extraData", extraData);
        _p.add(d, "TPMS_CLOCK_INFO", "clockInfo", clockInfo);
        _p.add(d, "long", "firmwareVersion", firmwareVersion);
        _p.add(d, "TPMU_ATTEST", "attested", attested);
    }
}

//<<<
