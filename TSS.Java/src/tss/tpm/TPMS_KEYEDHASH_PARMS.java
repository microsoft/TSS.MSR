package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure describes the parameters that would appear in the public area of a KEYEDHASH object.
*/
public class TPMS_KEYEDHASH_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS 
{
    /**
     * This structure describes the parameters that would appear in the public area of a KEYEDHASH object.
     * 
     * @param _scheme Indicates the signing method used for a keyedHash signing object. This field also determines the size of the data field for a data object created with TPM2_Create() or TPM2_CreatePrimary(). (One of TPMS_SCHEME_HMAC, TPMS_SCHEME_XOR, TPMS_NULL_SCHEME_KEYEDHASH)
     */
    public TPMS_KEYEDHASH_PARMS(TPMU_SCHEME_KEYEDHASH _scheme)
    {
        scheme = _scheme;
    }
    /**
    * This structure describes the parameters that would appear in the public area of a KEYEDHASH object.
    */
    public TPMS_KEYEDHASH_PARMS() {};
    /**
    * selects the scheme
    */
    // private TPM_ALG_ID schemeScheme;
    /**
    * Indicates the signing method used for a keyedHash signing object. This field also determines the size of the data field for a data object created with TPM2_Create() or TPM2_CreatePrimary().
    */
    public TPMU_SCHEME_KEYEDHASH scheme;
    public int GetUnionSelector_scheme()
    {
        if(scheme instanceof TPMS_SCHEME_HMAC){return 0x0005; }
        if(scheme instanceof TPMS_SCHEME_XOR){return 0x000A; }
        if(scheme instanceof TPMS_NULL_SCHEME_KEYEDHASH){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(GetUnionSelector_scheme(), 2);
        ((TpmMarshaller)scheme).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _schemeScheme = buf.readInt(2);
        scheme=null;
        if(_schemeScheme==TPM_ALG_ID.HMAC.toInt()) {scheme = new TPMS_SCHEME_HMAC();}
        else if(_schemeScheme==TPM_ALG_ID.XOR.toInt()) {scheme = new TPMS_SCHEME_XOR();}
        else if(_schemeScheme==TPM_ALG_ID.NULL.toInt()) {scheme = new TPMS_NULL_SCHEME_KEYEDHASH();}
        if(scheme==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_schemeScheme).name());
        scheme.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_KEYEDHASH_PARMS fromTpm (byte[] x) 
    {
        TPMS_KEYEDHASH_PARMS ret = new TPMS_KEYEDHASH_PARMS();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_KEYEDHASH_PARMS fromTpm (InByteBuf buf) 
    {
        TPMS_KEYEDHASH_PARMS ret = new TPMS_KEYEDHASH_PARMS();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_KEYEDHASH_PARMS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_SCHEME_KEYEDHASH", "scheme", scheme);
    };
    
    
};

//<<<

