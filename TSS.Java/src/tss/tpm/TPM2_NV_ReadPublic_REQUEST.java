package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to read the public area and Name of an NV Index. The public area
 *  of an Index is not privacy-sensitive and no authorization is required to read this data.
 */
public class TPM2_NV_ReadPublic_REQUEST extends ReqStructure
{
    /** The NV Index
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;
    
    public TPM2_NV_ReadPublic_REQUEST() { nvIndex = new TPM_HANDLE(); }
    
    /** @param _nvIndex The NV Index
     *         Auth Index: None
     */
    public TPM2_NV_ReadPublic_REQUEST(TPM_HANDLE _nvIndex) { nvIndex = _nvIndex; }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_ReadPublic_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_NV_ReadPublic_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_NV_ReadPublic_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_ReadPublic_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_NV_ReadPublic_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_ReadPublic_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {nvIndex}; }
}

//<<<
