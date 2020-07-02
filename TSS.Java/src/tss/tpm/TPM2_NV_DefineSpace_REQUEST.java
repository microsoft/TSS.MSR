package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command defines the attributes of an NV Index and causes the TPM to reserve space
 *  to hold the data associated with the NV Index. If a definition already exists at the
 *  NV Index, the TPM will return TPM_RC_NV_DEFINED.
 */
public class TPM2_NV_DefineSpace_REQUEST extends ReqStructure
{
    /** TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** The authorization value  */
    public byte[] auth;
    
    /** The public parameters of the NV area  */
    public TPMS_NV_PUBLIC publicInfo;
    
    public TPM2_NV_DefineSpace_REQUEST() { authHandle = new TPM_HANDLE(); }
    
    /** @param _authHandle TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _auth The authorization value
     *  @param _publicInfo The public parameters of the NV area
     */
    public TPM2_NV_DefineSpace_REQUEST(TPM_HANDLE _authHandle, byte[] _auth, TPMS_NV_PUBLIC _publicInfo)
    {
        authHandle = _authHandle;
        auth = _auth;
        publicInfo = _publicInfo;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(auth);
        buf.writeSizedObj(publicInfo);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        auth = buf.readSizedByteBuf();
        publicInfo = buf.createSizedObj(TPMS_NV_PUBLIC.class);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_DefineSpace_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_NV_DefineSpace_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_NV_DefineSpace_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_DefineSpace_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_NV_DefineSpace_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_DefineSpace_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "byte", "auth", auth);
        _p.add(d, "TPMS_NV_PUBLIC", "publicInfo", publicInfo);
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
