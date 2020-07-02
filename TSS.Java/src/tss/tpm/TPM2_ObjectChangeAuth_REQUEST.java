package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to change the authorization secret for a TPM-resident object.  */
public class TPM2_ObjectChangeAuth_REQUEST extends ReqStructure
{
    /** Handle of the object
     *  Auth Index: 1
     *  Auth Role: ADMIN
     */
    public TPM_HANDLE objectHandle;
    
    /** Handle of the parent
     *  Auth Index: None
     */
    public TPM_HANDLE parentHandle;
    
    /** New authorization value  */
    public byte[] newAuth;
    
    public TPM2_ObjectChangeAuth_REQUEST()
    {
        objectHandle = new TPM_HANDLE();
        parentHandle = new TPM_HANDLE();
    }
    
    /** @param _objectHandle Handle of the object
     *         Auth Index: 1
     *         Auth Role: ADMIN
     *  @param _parentHandle Handle of the parent
     *         Auth Index: None
     *  @param _newAuth New authorization value
     */
    public TPM2_ObjectChangeAuth_REQUEST(TPM_HANDLE _objectHandle, TPM_HANDLE _parentHandle, byte[] _newAuth)
    {
        objectHandle = _objectHandle;
        parentHandle = _parentHandle;
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
    public static TPM2_ObjectChangeAuth_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ObjectChangeAuth_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ObjectChangeAuth_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_ObjectChangeAuth_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ObjectChangeAuth_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ObjectChangeAuth_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "TPM_HANDLE", "parentHandle", parentHandle);
        _p.add(d, "byte", "newAuth", newAuth);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {objectHandle, parentHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
