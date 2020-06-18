package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The purpose of this command is to certify the contents of an NV Index or portion of an
 *  NV Index.
 */
public class TPM2_NV_Certify_REQUEST extends TpmStructure
{
    /** Handle of the key used to sign the attestation structure
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;
    
    /** Handle indicating the source of the authorization value for the NV Index
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** Index for the area to be certified
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;
    
    /** User-provided qualifying data  */
    public byte[] qualifyingData;
    
    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL  */
    public TPMU_SIG_SCHEME inScheme;
    
    /** Number of octets to certify  */
    public short size;
    
    /** Octet offset into the NV area
     *  This value shall be less than or equal to the size of the nvIndex data.
     */
    public short offset;
    
    public TPM2_NV_Certify_REQUEST()
    {
        signHandle = new TPM_HANDLE();
        authHandle = new TPM_HANDLE();
        nvIndex = new TPM_HANDLE();
    }
    
    /** @param _signHandle Handle of the key used to sign the attestation structure
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _authHandle Handle indicating the source of the authorization value for the NV Index
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _nvIndex Index for the area to be certified
     *         Auth Index: None
     *  @param _qualifyingData User-provided qualifying data
     *  @param _inScheme Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         (One of [TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *         TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME])
     *  @param _size Number of octets to certify
     *  @param _offset Octet offset into the NV area
     *         This value shall be less than or equal to the size of the nvIndex data.
     */
    public TPM2_NV_Certify_REQUEST(TPM_HANDLE _signHandle, TPM_HANDLE _authHandle, TPM_HANDLE _nvIndex, byte[] _qualifyingData, TPMU_SIG_SCHEME _inScheme, int _size, int _offset)
    {
        signHandle = _signHandle;
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        qualifyingData = _qualifyingData;
        inScheme = _inScheme;
        size = (short)_size;
        offset = (short)_offset;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(qualifyingData);
        inScheme.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)inScheme).toTpm(buf);
        buf.writeShort(size);
        buf.writeShort(offset);
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
        size = buf.readShort();
        offset = buf.readShort();
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_NV_Certify_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_NV_Certify_REQUEST ret = new TPM2_NV_Certify_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_NV_Certify_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_NV_Certify_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_Certify_REQUEST ret = new TPM2_NV_Certify_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_Certify_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "byte", "qualifyingData", qualifyingData);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
        _p.add(d, "short", "size", size);
        _p.add(d, "short", "offset", offset);
    }
}

//<<<
