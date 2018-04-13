package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used for a hash signing object.
*/
public class TPMT_KEYEDHASH_SCHEME extends TpmStructure
{
    /**
     * This structure is used for a hash signing object.
     * 
     * @param _details the scheme parameters (One of TPMS_SCHEME_HMAC, TPMS_SCHEME_XOR, TPMS_NULL_SCHEME_KEYEDHASH)
     */
    public TPMT_KEYEDHASH_SCHEME(TPMU_SCHEME_KEYEDHASH _details)
    {
        details = _details;
    }
    /**
    * This structure is used for a hash signing object.
    */
    public TPMT_KEYEDHASH_SCHEME() {};
    /**
    * selects the scheme
    */
    // private TPM_ALG_ID scheme;
    /**
    * the scheme parameters
    */
    public TPMU_SCHEME_KEYEDHASH details;
    public int GetUnionSelector_details()
    {
        if(details instanceof TPMS_SCHEME_HMAC){return 0x0005; }
        if(details instanceof TPMS_SCHEME_XOR){return 0x000A; }
        if(details instanceof TPMS_NULL_SCHEME_KEYEDHASH){return 0x0010; }
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
        if(_scheme==TPM_ALG_ID.HMAC.toInt()) {details = new TPMS_SCHEME_HMAC();}
        else if(_scheme==TPM_ALG_ID.XOR.toInt()) {details = new TPMS_SCHEME_XOR();}
        else if(_scheme==TPM_ALG_ID.NULL.toInt()) {details = new TPMS_NULL_SCHEME_KEYEDHASH();}
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
    public static TPMT_KEYEDHASH_SCHEME fromTpm (byte[] x) 
    {
        TPMT_KEYEDHASH_SCHEME ret = new TPMT_KEYEDHASH_SCHEME();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMT_KEYEDHASH_SCHEME fromTpm (InByteBuf buf) 
    {
        TPMT_KEYEDHASH_SCHEME ret = new TPMT_KEYEDHASH_SCHEME();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_KEYEDHASH_SCHEME");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_SCHEME_KEYEDHASH", "details", details);
    };
    
    
};

//<<<

