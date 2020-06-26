package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows setting of the authorization policy for the lockout
 *  (lockoutPolicy), the platform hierarchy (platformPolicy), the storage hierarchy
 *  (ownerPolicy), and the endorsement hierarchy (endorsementPolicy). On TPMs implementing
 *  Authenticated Countdown Timers (ACT), this command may also be used to set the
 *  authorization policy for an ACT.
 */
public class TPM2_SetPrimaryPolicy_REQUEST extends TpmStructure
{
    /** TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPMI_RH_ACT or TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** An authorization policy digest; may be the Empty Buffer
     *  If hashAlg is TPM_ALG_NULL, then this shall be an Empty Buffer.
     */
    public byte[] authPolicy;
    
    /** The hash algorithm to use for the policy
     *  If the authPolicy is an Empty Buffer, then this field shall be TPM_ALG_NULL.
     */
    public TPM_ALG_ID hashAlg;
    
    public TPM2_SetPrimaryPolicy_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        hashAlg = TPM_ALG_ID.NULL;
    }
    
    /** @param _authHandle TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPMI_RH_ACT or
     *         TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _authPolicy An authorization policy digest; may be the Empty Buffer
     *         If hashAlg is TPM_ALG_NULL, then this shall be an Empty Buffer.
     *  @param _hashAlg The hash algorithm to use for the policy
     *         If the authPolicy is an Empty Buffer, then this field shall be TPM_ALG_NULL.
     */
    public TPM2_SetPrimaryPolicy_REQUEST(TPM_HANDLE _authHandle, byte[] _authPolicy, TPM_ALG_ID _hashAlg)
    {
        authHandle = _authHandle;
        authPolicy = _authPolicy;
        hashAlg = _hashAlg;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(authPolicy);
        hashAlg.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        authPolicy = buf.readSizedByteBuf();
        hashAlg = TPM_ALG_ID.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_SetPrimaryPolicy_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_SetPrimaryPolicy_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_SetPrimaryPolicy_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_SetPrimaryPolicy_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_SetPrimaryPolicy_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SetPrimaryPolicy_REQUEST");
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
    }
}

//<<<
