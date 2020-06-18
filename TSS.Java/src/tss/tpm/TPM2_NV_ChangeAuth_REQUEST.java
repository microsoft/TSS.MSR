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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(newAuth);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _newAuthSize = buf.readShort() & 0xFFFF;
        newAuth = new byte[_newAuthSize];
        buf.readArrayOfInts(newAuth, 1, _newAuthSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_NV_ChangeAuth_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_NV_ChangeAuth_REQUEST ret = new TPM2_NV_ChangeAuth_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_NV_ChangeAuth_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_NV_ChangeAuth_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_ChangeAuth_REQUEST ret = new TPM2_NV_ChangeAuth_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
