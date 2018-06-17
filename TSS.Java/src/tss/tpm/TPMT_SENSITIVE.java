package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* authValue shall not be larger than the size of the digest produced by the nameAlg of the object. seedValue shall be the size of the digest produced by the nameAlg of the object.
*/
public class TPMT_SENSITIVE extends TpmStructure
{
    /**
     * authValue shall not be larger than the size of the digest produced by the nameAlg of the object. seedValue shall be the size of the digest produced by the nameAlg of the object.
     * 
     * @param _authValue user authorization data The authValue may be a zero-length string. 
     * @param _seedValue for a parent object, the optional protection seed; for other objects, the obfuscation value 
     * @param _sensitive the type-specific private data (One of TPM2B_PRIVATE_KEY_RSA, TPM2B_ECC_PARAMETER, TPM2B_SENSITIVE_DATA, TPM2B_SYM_KEY, TPM2B_PRIVATE_VENDOR_SPECIFIC)
     */
    public TPMT_SENSITIVE(byte[] _authValue,byte[] _seedValue,TPMU_SENSITIVE_COMPOSITE _sensitive)
    {
        authValue = _authValue;
        seedValue = _seedValue;
        sensitive = _sensitive;
    }
    /**
    * authValue shall not be larger than the size of the digest produced by the nameAlg of the object. seedValue shall be the size of the digest produced by the nameAlg of the object.
    */
    public TPMT_SENSITIVE() {};
    /**
    * identifier for the sensitive area This shall be the same as the type parameter of the associated public area.
    */
    // private TPM_ALG_ID sensitiveType;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short authValueSize;
    /**
    * user authorization data The authValue may be a zero-length string.
    */
    public byte[] authValue;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short seedValueSize;
    /**
    * for a parent object, the optional protection seed; for other objects, the obfuscation value
    */
    public byte[] seedValue;
    /**
    * the type-specific private data
    */
    public TPMU_SENSITIVE_COMPOSITE sensitive;
    public int GetUnionSelector_sensitive()
    {
        if(sensitive instanceof TPM2B_PRIVATE_KEY_RSA){return 0x0001; }
        if(sensitive instanceof TPM2B_ECC_PARAMETER){return 0x0023; }
        if(sensitive instanceof TPM2B_SENSITIVE_DATA){return 0x0008; }
        if(sensitive instanceof TPM2B_SYM_KEY){return 0x0025; }
        if(sensitive instanceof TPM2B_PRIVATE_VENDOR_SPECIFIC){return 0x7FFF; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(GetUnionSelector_sensitive(), 2);
        buf.writeInt((authValue!=null)?authValue.length:0, 2);
        if(authValue!=null)
            buf.write(authValue);
        buf.writeInt((seedValue!=null)?seedValue.length:0, 2);
        if(seedValue!=null)
            buf.write(seedValue);
        ((TpmMarshaller)sensitive).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _sensitiveType = buf.readInt(2);
        int _authValueSize = buf.readInt(2);
        authValue = new byte[_authValueSize];
        buf.readArrayOfInts(authValue, 1, _authValueSize);
        int _seedValueSize = buf.readInt(2);
        seedValue = new byte[_seedValueSize];
        buf.readArrayOfInts(seedValue, 1, _seedValueSize);
        sensitive=null;
        if(_sensitiveType==TPM_ALG_ID.RSA.toInt()) {sensitive = new TPM2B_PRIVATE_KEY_RSA();}
        else if(_sensitiveType==TPM_ALG_ID.ECC.toInt()) {sensitive = new TPM2B_ECC_PARAMETER();}
        else if(_sensitiveType==TPM_ALG_ID.KEYEDHASH.toInt()) {sensitive = new TPM2B_SENSITIVE_DATA();}
        else if(_sensitiveType==TPM_ALG_ID.SYMCIPHER.toInt()) {sensitive = new TPM2B_SYM_KEY();}
        else if(_sensitiveType==TPM_ALG_ID.ANY.toInt()) {sensitive = new TPM2B_PRIVATE_VENDOR_SPECIFIC();}
        if(sensitive==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_sensitiveType).name());
        sensitive.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMT_SENSITIVE fromTpm (byte[] x) 
    {
        TPMT_SENSITIVE ret = new TPMT_SENSITIVE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMT_SENSITIVE fromTpm (InByteBuf buf) 
    {
        TPMT_SENSITIVE ret = new TPMT_SENSITIVE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SENSITIVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "authValue", authValue);
        _p.add(d, "byte", "seedValue", seedValue);
        _p.add(d, "TPMU_SENSITIVE_COMPOSITE", "sensitive", sensitive);
    };
    
    
};

//<<<

