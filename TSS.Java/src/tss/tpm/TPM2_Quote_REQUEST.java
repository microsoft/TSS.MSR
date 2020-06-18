package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to quote PCR values.  */
public class TPM2_Quote_REQUEST extends TpmStructure
{
    /** Handle of key that will perform signature
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;
    
    /** Data supplied by the caller  */
    public byte[] qualifyingData;
    
    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL  */
    public TPMU_SIG_SCHEME inScheme;
    
    /** PCR set to quote  */
    public TPMS_PCR_SELECTION[] PCRselect;
    
    public TPM2_Quote_REQUEST() { signHandle = new TPM_HANDLE(); }
    
    /** @param _signHandle Handle of key that will perform signature
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _qualifyingData Data supplied by the caller
     *  @param _inScheme Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         (One of [TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *         TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME])
     *  @param _PCRselect PCR set to quote
     */
    public TPM2_Quote_REQUEST(TPM_HANDLE _signHandle, byte[] _qualifyingData, TPMU_SIG_SCHEME _inScheme, TPMS_PCR_SELECTION[] _PCRselect)
    {
        signHandle = _signHandle;
        qualifyingData = _qualifyingData;
        inScheme = _inScheme;
        PCRselect = _PCRselect;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(qualifyingData);
        inScheme.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)inScheme).toTpm(buf);
        buf.writeObjArr(PCRselect);
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
        int _PCRselectCount = buf.readInt();
        PCRselect = new TPMS_PCR_SELECTION[_PCRselectCount];
        for (int j=0; j < _PCRselectCount; j++) PCRselect[j] = new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(PCRselect, _PCRselectCount);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_Quote_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_Quote_REQUEST ret = new TPM2_Quote_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Quote_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_Quote_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Quote_REQUEST ret = new TPM2_Quote_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Quote_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "byte", "qualifyingData", qualifyingData);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
        _p.add(d, "TPMS_PCR_SELECTION", "PCRselect", PCRselect);
    }
}

//<<<
