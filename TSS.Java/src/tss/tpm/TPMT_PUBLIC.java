package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 191 defines the public area structure. The Name of the object is nameAlg concatenated with the digest of this structure using nameAlg.
*/
public class TPMT_PUBLIC extends TpmStructure
{
    /**
     * Table 191 defines the public area structure. The Name of the object is nameAlg concatenated with the digest of this structure using nameAlg.
     * 
     * @param _nameAlg algorithm used for computing the Name of the object NOTE The "+" indicates that the instance of a TPMT_PUBLIC may have a "+" to indicate that the nameAlg may be TPM_ALG_NULL. 
     * @param _objectAttributes attributes that, along with type, determine the manipulations of this object 
     * @param _authPolicy optional policy for using this key The policy is computed using the nameAlg of the object. NOTE Shall be the Empty Policy if no authorization policy is present. 
     * @param _parameters the algorithm or structure details (One of TPMS_KEYEDHASH_PARMS, TPMS_SYMCIPHER_PARMS, TPMS_RSA_PARMS, TPMS_ECC_PARMS, TPMS_ASYM_PARMS) 
     * @param _unique the unique identifier of the structure For an asymmetric key, this would be the public key. (One of TPM2B_DIGEST_Keyedhash, TPM2B_DIGEST_Symcipher, TPM2B_PUBLIC_KEY_RSA, TPMS_ECC_POINT, TPMS_DERIVE)
     */
    public TPMT_PUBLIC(TPM_ALG_ID _nameAlg,TPMA_OBJECT _objectAttributes,byte[] _authPolicy,TPMU_PUBLIC_PARMS _parameters,TPMU_PUBLIC_ID _unique)
    {
        nameAlg = _nameAlg;
        objectAttributes = _objectAttributes;
        authPolicy = _authPolicy;
        parameters = _parameters;
        unique = _unique;
    }
    /**
    * Table 191 defines the public area structure. The Name of the object is nameAlg concatenated with the digest of this structure using nameAlg.
    */
    public TPMT_PUBLIC() {};
    /**
    * algorithm associated with this object
    */
    // private TPM_ALG_ID type;
    /**
    * algorithm used for computing the Name of the object NOTE The "+" indicates that the instance of a TPMT_PUBLIC may have a "+" to indicate that the nameAlg may be TPM_ALG_NULL.
    */
    public TPM_ALG_ID nameAlg;
    /**
    * attributes that, along with type, determine the manipulations of this object
    */
    public TPMA_OBJECT objectAttributes;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short authPolicySize;
    /**
    * optional policy for using this key The policy is computed using the nameAlg of the object. NOTE Shall be the Empty Policy if no authorization policy is present.
    */
    public byte[] authPolicy;
    /**
    * the algorithm or structure details
    */
    public TPMU_PUBLIC_PARMS parameters;
    /**
    * the unique identifier of the structure For an asymmetric key, this would be the public key.
    */
    public TPMU_PUBLIC_ID unique;
    public int GetUnionSelector_parameters()
    {
        if(parameters instanceof TPMS_KEYEDHASH_PARMS){return 0x0008; }
        if(parameters instanceof TPMS_SYMCIPHER_PARMS){return 0x0025; }
        if(parameters instanceof TPMS_RSA_PARMS){return 0x0001; }
        if(parameters instanceof TPMS_ECC_PARMS){return 0x0023; }
        if(parameters instanceof TPMS_ASYM_PARMS){return 0x7FFF; }
        throw new RuntimeException("Unrecognized type");
    }
    public int GetUnionSelector_unique()
    {
        if(unique instanceof TPM2B_DIGEST_Keyedhash){return 0x0008; }
        if(unique instanceof TPM2B_DIGEST_Symcipher){return 0x0025; }
        if(unique instanceof TPM2B_PUBLIC_KEY_RSA){return 0x0001; }
        if(unique instanceof TPMS_ECC_POINT){return 0x0023; }
        if(unique instanceof TPMS_DERIVE){return 0x7FFF; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(GetUnionSelector_parameters(), 2);
        nameAlg.toTpm(buf);
        objectAttributes.toTpm(buf);
        buf.writeInt((authPolicy!=null)?authPolicy.length:0, 2);
        if(authPolicy!=null)
            buf.write(authPolicy);
        ((TpmMarshaller)parameters).toTpm(buf);
        ((TpmMarshaller)unique).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _type = buf.readInt(2);
        nameAlg = TPM_ALG_ID.fromTpm(buf);
        int _objectAttributes = buf.readInt(4);
        objectAttributes = TPMA_OBJECT.fromInt(_objectAttributes);
        int _authPolicySize = buf.readInt(2);
        authPolicy = new byte[_authPolicySize];
        buf.readArrayOfInts(authPolicy, 1, _authPolicySize);
        parameters=null;
        if(_type==TPM_ALG_ID.KEYEDHASH.toInt()) {parameters = new TPMS_KEYEDHASH_PARMS();}
        else if(_type==TPM_ALG_ID.SYMCIPHER.toInt()) {parameters = new TPMS_SYMCIPHER_PARMS();}
        else if(_type==TPM_ALG_ID.RSA.toInt()) {parameters = new TPMS_RSA_PARMS();}
        else if(_type==TPM_ALG_ID.ECC.toInt()) {parameters = new TPMS_ECC_PARMS();}
        else if(_type==TPM_ALG_ID.ANY.toInt()) {parameters = new TPMS_ASYM_PARMS();}
        if(parameters==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_type).name());
        parameters.initFromTpm(buf);
        unique=null;
        if(_type==TPM_ALG_ID.KEYEDHASH.toInt()) {unique = new TPM2B_DIGEST_Keyedhash();}
        else if(_type==TPM_ALG_ID.SYMCIPHER.toInt()) {unique = new TPM2B_DIGEST_Symcipher();}
        else if(_type==TPM_ALG_ID.RSA.toInt()) {unique = new TPM2B_PUBLIC_KEY_RSA();}
        else if(_type==TPM_ALG_ID.ECC.toInt()) {unique = new TPMS_ECC_POINT();}
        else if(_type==TPM_ALG_ID.ANY.toInt()) {unique = new TPMS_DERIVE();}
        if(unique==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_type).name());
        unique.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMT_PUBLIC fromTpm (byte[] x) 
    {
        TPMT_PUBLIC ret = new TPMT_PUBLIC();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMT_PUBLIC fromTpm (InByteBuf buf) 
    {
        TPMT_PUBLIC ret = new TPMT_PUBLIC();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_PUBLIC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "nameAlg", nameAlg);
        _p.add(d, "TPMA_OBJECT", "objectAttributes", objectAttributes);
        _p.add(d, "byte", "authPolicy", authPolicy);
        _p.add(d, "TPMU_PUBLIC_PARMS", "parameters", parameters);
        _p.add(d, "TPMU_PUBLIC_ID", "unique", unique);
    };
    
    /**
     * Validate a TPM signature.  Note that this function hashes dataThatWasSigned before
     * verifying the signature.
     *
     * @param _dataThatWasSigned The data
     * @param _signature The TPM signature
     * @return True if the signature is valid 
     */
    public boolean validateSignature(byte[] _dataThatWasSigned, TPMU_SIGNATURE _signature)
    {
    	return Crypto.validateSignature(this, _dataThatWasSigned, _signature);
    }
    
    public byte[] encrypt(byte[] inData, String label)
    {
    	return Crypto.asymEncrypt(this, inData, label);
    }
    
    /**
     * Returns the TPM name of this object.  The name is the alg-prepended hash of the public area.
     *
     * @return The TPM object name
     */
    public byte[] getName()
    {
       	byte[] pub = toTpm();
        byte[] pubHash = Crypto.hash(nameAlg, pub);
        byte[] theHashAlg = Helpers.hostToNet((short)nameAlg.toInt());
        return Helpers.concatenate(theHashAlg, pubHash);
    }
    /**
     * Validate a TPM quote against a set of PCR and a nonce.
     * 
     * @param expectedPcrs PCR values expected
     * @param nonce The nonce
     * @param quote The TPM generated quote
     * @return Whether the quote was valid
     * 
     */
    public boolean validateQuote(PCR_ReadResponse expectedPcrs, byte[] nonce, QuoteResponse quote)
    {
    	return Crypto.validateQuote(this, expectedPcrs, nonce, quote);
    }
    
    
};

//<<<

