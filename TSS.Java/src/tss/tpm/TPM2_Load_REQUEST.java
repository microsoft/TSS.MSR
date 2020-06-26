package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to load objects into the TPM. This command is used when both a
 *  TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be
 *  loaded, the TPM2_LoadExternal command is used.
 */
public class TPM2_Load_REQUEST extends TpmStructure
{
    /** TPM handle of parent key; shall not be a reserved handle
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE parentHandle;
    
    /** The private portion of the object  */
    public TPM2B_PRIVATE inPrivate;
    
    /** The public portion of the object  */
    public TPMT_PUBLIC inPublic;
    
    public TPM2_Load_REQUEST() { parentHandle = new TPM_HANDLE(); }
    
    /** @param _parentHandle TPM handle of parent key; shall not be a reserved handle
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _inPrivate The private portion of the object
     *  @param _inPublic The public portion of the object
     */
    public TPM2_Load_REQUEST(TPM_HANDLE _parentHandle, TPM2B_PRIVATE _inPrivate, TPMT_PUBLIC _inPublic)
    {
        parentHandle = _parentHandle;
        inPrivate = _inPrivate;
        inPublic = _inPublic;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        inPrivate.toTpm(buf);
        buf.writeSizedObj(inPublic);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        inPrivate = TPM2B_PRIVATE.fromTpm(buf);
        inPublic = buf.createSizedObj(TPMT_PUBLIC.class);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_Load_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Load_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Load_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_Load_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Load_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Load_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "parentHandle", parentHandle);
        _p.add(d, "TPM2B_PRIVATE", "inPrivate", inPrivate);
        _p.add(d, "TPMT_PUBLIC", "inPublic", inPublic);
    }
}

//<<<
