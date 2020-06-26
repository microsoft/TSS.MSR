package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows certain Transient Objects to be made persistent or a persistent
 *  object to be evicted.
 */
public class TPM2_EvictControl_REQUEST extends TpmStructure
{
    /** TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *  Auth Handle: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE auth;
    
    /** The handle of a loaded object
     *  Auth Index: None
     */
    public TPM_HANDLE objectHandle;
    
    /** If objectHandle is a transient object handle, then this is the persistent handle for
     *  the object
     *  if objectHandle is a persistent object handle, then it shall be the same value as
     *  persistentHandle
     */
    public TPM_HANDLE persistentHandle;
    
    public TPM2_EvictControl_REQUEST()
    {
        auth = new TPM_HANDLE();
        objectHandle = new TPM_HANDLE();
        persistentHandle = new TPM_HANDLE();
    }
    
    /** @param _auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *         Auth Handle: 1
     *         Auth Role: USER
     *  @param _objectHandle The handle of a loaded object
     *         Auth Index: None
     *  @param _persistentHandle If objectHandle is a transient object handle, then this is the
     *         persistent handle for the object
     *         if objectHandle is a persistent object handle, then it shall be the same value as
     *         persistentHandle
     */
    public TPM2_EvictControl_REQUEST(TPM_HANDLE _auth, TPM_HANDLE _objectHandle, TPM_HANDLE _persistentHandle)
    {
        auth = _auth;
        objectHandle = _objectHandle;
        persistentHandle = _persistentHandle;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { persistentHandle.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { persistentHandle = TPM_HANDLE.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_EvictControl_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_EvictControl_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_EvictControl_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_EvictControl_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_EvictControl_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EvictControl_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "TPM_HANDLE", "persistentHandle", persistentHandle);
    }
}

//<<<
