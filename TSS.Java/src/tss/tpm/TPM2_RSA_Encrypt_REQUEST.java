package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command performs RSA encryption using the indicated padding scheme according to IETF
 *  RFC 8017. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to
 *  specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme
 *  shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
 */
public class TPM2_RSA_Encrypt_REQUEST extends TpmStructure
{
    /**
     *  reference to public portion of RSA key to use for encryption
     *  Auth Index: None
     */
    public TPM_HANDLE keyHandle;
    
    /**
     *  message to be encrypted
     *  NOTE 1 The data type was chosen because it limits the overall size of the input to no
     *  greater than the size of the largest RSA public key. This may be larger
     *  than allowed for keyHandle.
     */
    public byte[] message;
    
    /** scheme selector */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** the padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL */
    public TPMU_ASYM_SCHEME inScheme;
    
    /**
     *  optional label L to be associated with the message
     *  Size of the buffer is zero if no label is present
     *  NOTE 2 See description of label above.
     */
    public byte[] label;
    
    public TPM2_RSA_Encrypt_REQUEST() { keyHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _keyHandle reference to public portion of RSA key to use for encryption
     *         Auth Index: None
     *  @param _message message to be encrypted
     *         NOTE 1 The data type was chosen because it limits the overall size of the input to no
     *         greater than the size of the largest RSA public key. This may be larger
     *         than allowed for keyHandle.
     *  @param _inScheme the padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL
     *         (One of [TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2,
     *         TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP,
     *         TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME])
     *  @param _label optional label L to be associated with the message
     *         Size of the buffer is zero if no label is present
     *         NOTE 2 See description of label above.
     */
    public TPM2_RSA_Encrypt_REQUEST(TPM_HANDLE _keyHandle, byte[] _message, TPMU_ASYM_SCHEME _inScheme, byte[] _label)
    {
        keyHandle = _keyHandle;
        message = _message;
        inScheme = _inScheme;
        label = _label;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(message);
        inScheme.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)inScheme).toTpm(buf);
        buf.writeSizedByteBuf(label);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _messageSize = buf.readShort() & 0xFFFF;
        message = new byte[_messageSize];
        buf.readArrayOfInts(message, 1, _messageSize);
        int _inSchemeScheme = buf.readShort() & 0xFFFF;
        inScheme = UnionFactory.create("TPMU_ASYM_SCHEME", new TPM_ALG_ID(_inSchemeScheme));
        inScheme.initFromTpm(buf);
        int _labelSize = buf.readShort() & 0xFFFF;
        label = new byte[_labelSize];
        buf.readArrayOfInts(label, 1, _labelSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
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
    }
}

//<<<
