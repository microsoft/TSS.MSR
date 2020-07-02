package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to associate a policy with a PCR or group of PCR. The policy
 *  determines the conditions under which a PCR may be extended or reset.
 */
public class TPM2_PCR_SetAuthPolicy_REQUEST extends ReqStructure
{
    /** TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** The desired authPolicy  */
    public byte[] authPolicy;
    
    /** The hash algorithm of the policy  */
    public TPM_ALG_ID hashAlg;
    
    /** The PCR for which the policy is to be set  */
    public TPM_HANDLE pcrNum;
    
    public TPM2_PCR_SetAuthPolicy_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        hashAlg = TPM_ALG_ID.NULL;
        pcrNum = new TPM_HANDLE();
    }
    
    /** @param _authHandle TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _authPolicy The desired authPolicy
     *  @param _hashAlg The hash algorithm of the policy
     *  @param _pcrNum The PCR for which the policy is to be set
     */
    public TPM2_PCR_SetAuthPolicy_REQUEST(TPM_HANDLE _authHandle, byte[] _authPolicy, TPM_ALG_ID _hashAlg, TPM_HANDLE _pcrNum)
    {
        authHandle = _authHandle;
        authPolicy = _authPolicy;
        hashAlg = _hashAlg;
        pcrNum = _pcrNum;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(authPolicy);
        hashAlg.toTpm(buf);
        pcrNum.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        authPolicy = buf.readSizedByteBuf();
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        pcrNum = TPM_HANDLE.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PCR_SetAuthPolicy_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PCR_SetAuthPolicy_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PCR_SetAuthPolicy_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PCR_SetAuthPolicy_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PCR_SetAuthPolicy_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_SetAuthPolicy_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "byte", "authPolicy", authPolicy);
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
        _p.add(d, "TPM_HANDLE", "pcrNum", pcrNum);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
