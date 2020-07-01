package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used when the TPM returns a list of loaded handles when the
 *  capability in TPM2_GetCapability() is TPM_CAP_HANDLE.
 */
public class TPML_HANDLE extends TpmStructure implements TPMU_CAPABILITIES
{
    /** An array of handles  */
    public TPM_HANDLE[] handle;
    
    public TPML_HANDLE() {}
    
    /** @param _handle An array of handles  */
    public TPML_HANDLE(TPM_HANDLE[] _handle) { handle = _handle; }
    
    /** TpmUnion method  */
    public TPM_CAP GetUnionSelector() { return TPM_CAP.HANDLES; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(handle); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { handle = buf.readObjArr(TPM_HANDLE.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPML_HANDLE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_HANDLE.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPML_HANDLE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPML_HANDLE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_HANDLE.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_HANDLE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
    }
}

//<<<
