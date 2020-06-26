package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used on each TPM-generated signed structure. The signature is over
 *  this structure.
 */
public class TPMS_ATTEST extends TpmStructure
{
    /** The indication that this structure was created by a TPM (always TPM_GENERATED_VALUE)  */
    public TPM_GENERATED magic;
    
    /** Type of the attestation structure  */
    public TPM_ST type() { return attested.GetUnionSelector(); }
    
    /** Qualified Name of the signing key  */
    public byte[] qualifiedSigner;
    
    /** External information supplied by caller
     *  NOTE A TPM2B_DATA structure provides room for a digest and a method indicator to
     *  indicate the components of the digest. The definition of this method indicator is
     *  outside the scope of this specification.
     */
    public byte[] extraData;
    
    /** Clock, resetCount, restartCount, and Safe  */
    public TPMS_CLOCK_INFO clockInfo;
    
    /** TPM-vendor-specific value identifying the version number of the firmware  */
    public long firmwareVersion;
    
    /** The type-specific attestation information  */
    public TPMU_ATTEST attested;
    
    public TPMS_ATTEST() {}
    
    /** @param _magic The indication that this structure was created by a TPM (always
     *  TPM_GENERATED_VALUE)
     *  @param _qualifiedSigner Qualified Name of the signing key
     *  @param _extraData External information supplied by caller
     *         NOTE A TPM2B_DATA structure provides room for a digest and a method indicator to
     *         indicate the components of the digest. The definition of this method indicator is
     *         outside the scope of this specification.
     *  @param _clockInfo Clock, resetCount, restartCount, and Safe
     *  @param _firmwareVersion TPM-vendor-specific value identifying the version number of
     *  the firmware
     *  @param _attested The type-specific attestation information
     *         (One of [TPMS_CERTIFY_INFO, TPMS_CREATION_INFO, TPMS_QUOTE_INFO,
     *         TPMS_COMMAND_AUDIT_INFO, TPMS_SESSION_AUDIT_INFO, TPMS_TIME_ATTEST_INFO,
     *         TPMS_NV_CERTIFY_INFO, TPMS_NV_DIGEST_CERTIFY_INFO])
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
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        magic.toTpm(buf);
        buf.writeShort(attested.GetUnionSelector());
        buf.writeSizedByteBuf(qualifiedSigner);
        buf.writeSizedByteBuf(extraData);
        clockInfo.toTpm(buf);
        buf.writeInt64(firmwareVersion);
        attested.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        magic = TPM_GENERATED.fromTpm(buf);
        TPM_ST type = TPM_ST.fromTpm(buf);
        qualifiedSigner = buf.readSizedByteBuf();
        extraData = buf.readSizedByteBuf();
        clockInfo = TPMS_CLOCK_INFO.fromTpm(buf);
        firmwareVersion = buf.readInt64();
        attested = UnionFactory.create("TPMU_ATTEST", type);
        attested.initFromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_ATTEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_ATTEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_ATTEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_ATTEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_ATTEST.class);
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
