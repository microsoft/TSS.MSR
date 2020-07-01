package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command enables and disables use of a hierarchy and its associated NV storage.
 *  The command allows phEnable, phEnableNV, shEnable, and ehEnable to be changed when the
 *  proper authorization is provided.
 */
public class TPM2_HierarchyControl_REQUEST extends ReqStructure
{
    /** TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** The enable being modified
     *  TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM, or TPM_RH_PLATFORM_NV
     */
    public TPM_HANDLE enable;
    
    /** YES if the enable should be SET, NO if the enable should be CLEAR  */
    public byte state;
    
    public TPM2_HierarchyControl_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        enable = new TPM_HANDLE();
    }
    
    /** @param _authHandle TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _enable The enable being modified
     *         TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM, or TPM_RH_PLATFORM_NV
     *  @param _state YES if the enable should be SET, NO if the enable should be CLEAR
     */
    public TPM2_HierarchyControl_REQUEST(TPM_HANDLE _authHandle, TPM_HANDLE _enable, byte _state)
    {
        authHandle = _authHandle;
        enable = _enable;
        state = _state;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        enable.toTpm(buf);
        buf.writeByte(state);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        enable = TPM_HANDLE.fromTpm(buf);
        state = buf.readByte();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_HierarchyControl_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_HierarchyControl_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_HierarchyControl_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_HierarchyControl_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_HierarchyControl_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HierarchyControl_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "enable", enable);
        _p.add(d, "byte", "state", state);
    }

    @Override
    public int numHandles() { return 1; }
    
    public int numAuthHandles() { return 1; }
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authHandle}; }
}

//<<<
