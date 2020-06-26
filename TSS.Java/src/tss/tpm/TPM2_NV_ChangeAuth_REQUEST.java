package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows the authorization secret for an NV Index to be changed.  */
public class TPM2_NV_ChangeAuth_REQUEST extends TpmStructure
{
    /** Handle of the entity
     *  Auth Index: 1
     *  Auth Role: ADMIN
     */
    public TPM_HANDLE nvIndex;
    
    /** New authorization value  */
    public byte[] newAuth;
    
    public TPM2_NV_ChangeAuth_REQUEST() { nvIndex = new TPM_HANDLE(); }
    
    /** @param _nvIndex Handle of the entity
     *         Auth Index: 1
     *         Auth Role: ADMIN
     *  @param _newAuth New authorization value
     */
    public TPM2_NV_ChangeAuth_REQUEST(TPM_HANDLE _nvIndex, byte[] _newAuth)
    {
        nvIndex = _nvIndex;
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
    public static TPM2_NV_ChangeAuth_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_NV_ChangeAuth_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_NV_ChangeAuth_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_ChangeAuth_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_NV_ChangeAuth_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_ChangeAuth_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "byte", "newAuth", newAuth);
    }
}

//<<<
