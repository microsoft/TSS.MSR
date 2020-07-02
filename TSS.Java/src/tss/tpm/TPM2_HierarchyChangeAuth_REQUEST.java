package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows the authorization secret for a hierarchy or lockout to be changed
 *  using the current authorization value as the command authorization.
 */
public class TPM2_HierarchyChangeAuth_REQUEST extends ReqStructure
{
    /** TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** New authorization value  */
    public byte[] newAuth;
    
    public TPM2_HierarchyChangeAuth_REQUEST() { authHandle = new TPM_HANDLE(); }
    
    /** @param _authHandle TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _newAuth New authorization value
     */
    public TPM2_HierarchyChangeAuth_REQUEST(TPM_HANDLE _authHandle, byte[] _newAuth)
    {
        authHandle = _authHandle;
        newAuth = _newAuth;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(newAuth); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { newAuth = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_HierarchyChangeAuth_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_HierarchyChangeAuth_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_HierarchyChangeAuth_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_HierarchyChangeAuth_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_HierarchyChangeAuth_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HierarchyChangeAuth_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "byte", "newAuth", newAuth);
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
