package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to SET bits in an NV Index that was created as a bit field. Any
 *  number of bits from 0 to 64 may be SET. The contents of bits are ORed with the current
 *  contents of the NV Index.
 */
public class TPM2_NV_SetBits_REQUEST extends ReqStructure
{
    /** Handle indicating the source of the authorization value
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** NV Index of the area in which the bit is to be set
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;
    
    /** The data to OR with the current contents  */
    public long bits;
    
    public TPM2_NV_SetBits_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        nvIndex = new TPM_HANDLE();
    }
    
    /** @param _authHandle Handle indicating the source of the authorization value
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _nvIndex NV Index of the area in which the bit is to be set
     *         Auth Index: None
     *  @param _bits The data to OR with the current contents
     */
    public TPM2_NV_SetBits_REQUEST(TPM_HANDLE _authHandle, TPM_HANDLE _nvIndex, long _bits)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        bits = _bits;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeInt64(bits); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { bits = buf.readInt64(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_SetBits_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_NV_SetBits_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_NV_SetBits_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_SetBits_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_NV_SetBits_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_SetBits_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "long", "bits", bits);
    }

    @Override
    public int numHandles() { return 2; }
    
    public int numAuthHandles() { return 1; }
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authHandle, nvIndex}; }
}

//<<<
