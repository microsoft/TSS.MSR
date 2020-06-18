package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The purpose of this command is to prove that an object with a specific Name is loaded
 *  in the TPM. By certifying that the object is loaded, the TPM warrants that a public
 *  area with a given Name is self-consistent and associated with a valid sensitive area.
 *  If a relying party has a public area that has the same Name as a Name certified with
 *  this command, then the values in that public area are correct.
 */
public class TPM2_Certify_REQUEST extends TpmStructure
{
    /** Handle of the object to be certified
     *  Auth Index: 1
     *  Auth Role: ADMIN
     */
    public TPM_HANDLE objectHandle;
    
    /** Handle of the key used to sign the attestation structure
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;
    
    /** User provided qualifying data  */
    public byte[] qualifyingData;
    
    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL  */
    public TPMU_SIG_SCHEME inScheme;
    
    public TPM2_Certify_REQUEST()
    {
        objectHandle = new TPM_HANDLE();
        signHandle = new TPM_HANDLE();
    }
    
    /** @param _objectHandle Handle of the object to be certified
     *         Auth Index: 1
     *         Auth Role: ADMIN
     *  @param _signHandle Handle of the key used to sign the attestation structure
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _qualifyingData User provided qualifying data
     *  @param _inScheme Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         (One of [TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *         TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME])
     */
    public TPM2_Certify_REQUEST(TPM_HANDLE _objectHandle, TPM_HANDLE _signHandle, byte[] _qualifyingData, TPMU_SIG_SCHEME _inScheme)
    {
        objectHandle = _objectHandle;
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
    
    public static TPM2_Certify_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_Certify_REQUEST ret = new TPM2_Certify_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Certify_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_Certify_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Certify_REQUEST ret = new TPM2_Certify_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Certify_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "byte", "qualifyingData", qualifyingData);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
    }
}

//<<<
