package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 157 Definition of TPMT_KDF_SCHEME Structure
*/
public class TPMT_KDF_SCHEME extends TpmStructure
{
    /**
     * Table 157 Definition of TPMT_KDF_SCHEME Structure
     * 
     * @param _details scheme parameters (One of TPMS_SCHEME_MGF1, TPMS_SCHEME_KDF1_SP800_56A, TPMS_SCHEME_KDF2, TPMS_SCHEME_KDF1_SP800_108, TPMS_NULL_KDF_SCHEME)
     */
    public TPMT_KDF_SCHEME(TPMU_KDF_SCHEME _details)
    {
        details = _details;
    }
    /**
    * Table 157 Definition of TPMT_KDF_SCHEME Structure
    */
    public TPMT_KDF_SCHEME() {};
    /**
    * scheme selector
    */
    // private TPM_ALG_ID scheme;
    /**
    * scheme parameters
    */
    public TPMU_KDF_SCHEME details;
    public int GetUnionSelector_details()
    {
        if(details instanceof TPMS_SCHEME_MGF1){return 0x0007; }
        if(details instanceof TPMS_SCHEME_KDF1_SP800_56A){return 0x0020; }
        if(details instanceof TPMS_SCHEME_KDF2){return 0x0021; }
        if(details instanceof TPMS_SCHEME_KDF1_SP800_108){return 0x0022; }
        if(details instanceof TPMS_NULL_KDF_SCHEME){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(GetUnionSelector_details(), 2);
        ((TpmMarshaller)details).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _scheme = buf.readInt(2);
        details=null;
        if(_scheme==TPM_ALG_ID.MGF1.toInt()) {details = new TPMS_SCHEME_MGF1();}
        else if(_scheme==TPM_ALG_ID.KDF1_SP800_56A.toInt()) {details = new TPMS_SCHEME_KDF1_SP800_56A();}
        else if(_scheme==TPM_ALG_ID.KDF2.toInt()) {details = new TPMS_SCHEME_KDF2();}
        else if(_scheme==TPM_ALG_ID.KDF1_SP800_108.toInt()) {details = new TPMS_SCHEME_KDF1_SP800_108();}
        else if(_scheme==TPM_ALG_ID.NULL.toInt()) {details = new TPMS_NULL_KDF_SCHEME();}
        if(details==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_scheme).name());
        details.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMT_KDF_SCHEME fromTpm (byte[] x) 
    {
        TPMT_KDF_SCHEME ret = new TPMT_KDF_SCHEME();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMT_KDF_SCHEME fromTpm (InByteBuf buf) 
    {
        TPMT_KDF_SCHEME ret = new TPMT_KDF_SCHEME();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_KDF_SCHEME");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_KDF_SCHEME", "details", details);
    };
    
    
};

//<<<

