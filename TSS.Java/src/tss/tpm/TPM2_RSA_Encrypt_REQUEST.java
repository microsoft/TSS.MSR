package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command performs RSA encryption using the indicated padding scheme according to IETF RFC 8017. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
*/
public class TPM2_RSA_Encrypt_REQUEST extends TpmStructure
{
    /**
     * This command performs RSA encryption using the indicated padding scheme according to IETF RFC 8017. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
     * 
     * @param _keyHandle reference to public portion of RSA key to use for encryption Auth Index: None 
     * @param _message message to be encrypted NOTE 1 The data type was chosen because it limits the overall size of the input to no greater than the size of the largest RSA public key. This may be larger than allowed for keyHandle. 
     * @param _inScheme the padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL (One of TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME) 
     * @param _label optional label L to be associated with the message Size of the buffer is zero if no label is present NOTE 2 See description of label above.
     */
    public TPM2_RSA_Encrypt_REQUEST(TPM_HANDLE _keyHandle,byte[] _message,TPMU_ASYM_SCHEME _inScheme,byte[] _label)
    {
        keyHandle = _keyHandle;
        message = _message;
        inScheme = _inScheme;
        label = _label;
    }
    /**
    * This command performs RSA encryption using the indicated padding scheme according to IETF RFC 8017. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
    */
    public TPM2_RSA_Encrypt_REQUEST() {};
    /**
    * reference to public portion of RSA key to use for encryption Auth Index: None
    */
    public TPM_HANDLE keyHandle;
    /**
    * size of the buffer The value of zero is only valid for create.
    */
    // private short messageSize;
    /**
    * message to be encrypted NOTE 1 The data type was chosen because it limits the overall size of the input to no greater than the size of the largest RSA public key. This may be larger than allowed for keyHandle.
    */
    public byte[] message;
    /**
    * scheme selector
    */
    // private TPM_ALG_ID inSchemeScheme;
    /**
    * the padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL
    */
    public TPMU_ASYM_SCHEME inScheme;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short labelSize;
    /**
    * optional label L to be associated with the message Size of the buffer is zero if no label is present NOTE 2 See description of label above.
    */
    public byte[] label;
    public int GetUnionSelector_inScheme()
    {
        if(inScheme instanceof TPMS_KEY_SCHEME_ECDH){return 0x0019; }
        if(inScheme instanceof TPMS_KEY_SCHEME_ECMQV){return 0x001D; }
        if(inScheme instanceof TPMS_SIG_SCHEME_RSASSA){return 0x0014; }
        if(inScheme instanceof TPMS_SIG_SCHEME_RSAPSS){return 0x0016; }
        if(inScheme instanceof TPMS_SIG_SCHEME_ECDSA){return 0x0018; }
        if(inScheme instanceof TPMS_SIG_SCHEME_ECDAA){return 0x001A; }
        if(inScheme instanceof TPMS_SIG_SCHEME_SM2){return 0x001B; }
        if(inScheme instanceof TPMS_SIG_SCHEME_ECSCHNORR){return 0x001C; }
        if(inScheme instanceof TPMS_ENC_SCHEME_RSAES){return 0x0015; }
        if(inScheme instanceof TPMS_ENC_SCHEME_OAEP){return 0x0017; }
        if(inScheme instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(inScheme instanceof TPMS_NULL_ASYM_SCHEME){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        keyHandle.toTpm(buf);
        buf.writeInt((message!=null)?message.length:0, 2);
        if(message!=null)
            buf.write(message);
        buf.writeInt(GetUnionSelector_inScheme(), 2);
        ((TpmMarshaller)inScheme).toTpm(buf);
        buf.writeInt((label!=null)?label.length:0, 2);
        if(label!=null)
            buf.write(label);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        keyHandle = TPM_HANDLE.fromTpm(buf);
        int _messageSize = buf.readInt(2);
        message = new byte[_messageSize];
        buf.readArrayOfInts(message, 1, _messageSize);
        int _inSchemeScheme = buf.readInt(2);
        inScheme=null;
        if(_inSchemeScheme==TPM_ALG_ID.ECDH.toInt()) {inScheme = new TPMS_KEY_SCHEME_ECDH();}
        else if(_inSchemeScheme==TPM_ALG_ID.ECMQV.toInt()) {inScheme = new TPMS_KEY_SCHEME_ECMQV();}
        else if(_inSchemeScheme==TPM_ALG_ID.RSASSA.toInt()) {inScheme = new TPMS_SIG_SCHEME_RSASSA();}
        else if(_inSchemeScheme==TPM_ALG_ID.RSAPSS.toInt()) {inScheme = new TPMS_SIG_SCHEME_RSAPSS();}
        else if(_inSchemeScheme==TPM_ALG_ID.ECDSA.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECDSA();}
        else if(_inSchemeScheme==TPM_ALG_ID.ECDAA.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_inSchemeScheme==TPM_ALG_ID.SM2.toInt()) {inScheme = new TPMS_SIG_SCHEME_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_inSchemeScheme==TPM_ALG_ID.ECSCHNORR.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECSCHNORR();}
        else if(_inSchemeScheme==TPM_ALG_ID.RSAES.toInt()) {inScheme = new TPMS_ENC_SCHEME_RSAES();}
        else if(_inSchemeScheme==TPM_ALG_ID.OAEP.toInt()) {inScheme = new TPMS_ENC_SCHEME_OAEP();}
        else if(_inSchemeScheme==TPM_ALG_ID.ANY.toInt()) {inScheme = new TPMS_SCHEME_HASH();}
        else if(_inSchemeScheme==TPM_ALG_ID.NULL.toInt()) {inScheme = new TPMS_NULL_ASYM_SCHEME();}
        if(inScheme==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_inSchemeScheme).name());
        inScheme.initFromTpm(buf);
        int _labelSize = buf.readInt(2);
        label = new byte[_labelSize];
        buf.readArrayOfInts(label, 1, _labelSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_RSA_Encrypt_REQUEST fromTpm (byte[] x) 
    {
        TPM2_RSA_Encrypt_REQUEST ret = new TPM2_RSA_Encrypt_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_RSA_Encrypt_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_RSA_Encrypt_REQUEST ret = new TPM2_RSA_Encrypt_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_RSA_Encrypt_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte", "message", message);
        _p.add(d, "TPMU_ASYM_SCHEME", "inScheme", inScheme);
        _p.add(d, "byte", "label", label);
    };
    
    
};

//<<<

