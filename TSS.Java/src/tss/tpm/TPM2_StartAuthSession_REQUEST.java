package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to start an authorization session using alternative methods of
 *  establishing the session key (sessionKey). The session key is then used to derive
 *  values used for authorization and for encrypting parameters.
 */
public class TPM2_StartAuthSession_REQUEST extends TpmStructure
{
    /** Handle of a loaded decrypt key used to encrypt salt
     *  may be TPM_RH_NULL
     *  Auth Index: None
     */
    public TPM_HANDLE tpmKey;
    
    /** Entity providing the authValue
     *  may be TPM_RH_NULL
     *  Auth Index: None
     */
    public TPM_HANDLE bind;
    
    /** Initial nonceCaller, sets nonceTPM size for the session
     *  shall be at least 16 octets
     */
    public byte[] nonceCaller;
    
    /** Value encrypted according to the type of tpmKey
     *  If tpmKey is TPM_RH_NULL, this shall be the Empty Buffer.
     */
    public byte[] encryptedSalt;
    
    /** Indicates the type of the session; simple HMAC or policy (including a trial policy)  */
    public TPM_SE sessionType;
    
    /** The algorithm and key size for parameter encryption
     *  may select TPM_ALG_NULL
     */
    public TPMT_SYM_DEF symmetric;
    
    /** Hash algorithm to use for the session
     *  Shall be a hash algorithm supported by the TPM and not TPM_ALG_NULL
     */
    public TPM_ALG_ID authHash;
    
    public TPM2_StartAuthSession_REQUEST()
    {
        tpmKey = new TPM_HANDLE();
        bind = new TPM_HANDLE();
        authHash = TPM_ALG_ID.NULL;
    }
    
    /** @param _tpmKey Handle of a loaded decrypt key used to encrypt salt
     *         may be TPM_RH_NULL
     *         Auth Index: None
     *  @param _bind Entity providing the authValue
     *         may be TPM_RH_NULL
     *         Auth Index: None
     *  @param _nonceCaller Initial nonceCaller, sets nonceTPM size for the session
     *         shall be at least 16 octets
     *  @param _encryptedSalt Value encrypted according to the type of tpmKey
     *         If tpmKey is TPM_RH_NULL, this shall be the Empty Buffer.
     *  @param _sessionType Indicates the type of the session; simple HMAC or policy
     *  (including a
     *         trial policy)
     *  @param _symmetric The algorithm and key size for parameter encryption
     *         may select TPM_ALG_NULL
     *  @param _authHash Hash algorithm to use for the session
     *         Shall be a hash algorithm supported by the TPM and not TPM_ALG_NULL
     */
    public TPM2_StartAuthSession_REQUEST(TPM_HANDLE _tpmKey, TPM_HANDLE _bind, byte[] _nonceCaller, byte[] _encryptedSalt, TPM_SE _sessionType, TPMT_SYM_DEF _symmetric, TPM_ALG_ID _authHash)
    {
        tpmKey = _tpmKey;
        bind = _bind;
        nonceCaller = _nonceCaller;
        encryptedSalt = _encryptedSalt;
        sessionType = _sessionType;
        symmetric = _symmetric;
        authHash = _authHash;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(nonceCaller);
        buf.writeSizedByteBuf(encryptedSalt);
        sessionType.toTpm(buf);
        symmetric.toTpm(buf);
        authHash.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _nonceCallerSize = buf.readShort() & 0xFFFF;
        nonceCaller = new byte[_nonceCallerSize];
        buf.readArrayOfInts(nonceCaller, 1, _nonceCallerSize);
        int _encryptedSaltSize = buf.readShort() & 0xFFFF;
        encryptedSalt = new byte[_encryptedSaltSize];
        buf.readArrayOfInts(encryptedSalt, 1, _encryptedSaltSize);
        sessionType = TPM_SE.fromTpm(buf);
        symmetric = TPMT_SYM_DEF.fromTpm(buf);
        authHash = TPM_ALG_ID.fromTpm(buf);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_StartAuthSession_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_StartAuthSession_REQUEST ret = new TPM2_StartAuthSession_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_StartAuthSession_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_StartAuthSession_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_StartAuthSession_REQUEST ret = new TPM2_StartAuthSession_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_StartAuthSession_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "tpmKey", tpmKey);
        _p.add(d, "TPM_HANDLE", "bind", bind);
        _p.add(d, "byte", "nonceCaller", nonceCaller);
        _p.add(d, "byte", "encryptedSalt", encryptedSalt);
        _p.add(d, "TPM_SE", "sessionType", sessionType);
        _p.add(d, "TPMT_SYM_DEF", "symmetric", symmetric);
        _p.add(d, "TPM_ALG_ID", "authHash", authHash);
    }
}

//<<<
