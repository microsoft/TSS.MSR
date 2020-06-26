package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to create an object that can be loaded into a TPM using
 *  TPM2_Load(). If the command completes successfully, the TPM will create the new object
 *  and return the objects creation data (creationData), its public area (outPublic), and
 *  its encrypted sensitive area (outPrivate). Preservation of the returned data is the
 *  responsibility of the caller. The object will need to be loaded (TPM2_Load()) before
 *  it may be used. The only difference between the inPublic TPMT_PUBLIC template and the
 *  outPublic TPMT_PUBLIC object is in the unique field.
 */
public class TPM2_Create_REQUEST extends TpmStructure
{
    /** Handle of parent for new object
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE parentHandle;
    
    /** The sensitive data  */
    public TPMS_SENSITIVE_CREATE inSensitive;
    
    /** The public template  */
    public TPMT_PUBLIC inPublic;
    
    /** Data that will be included in the creation data for this object to provide permanent,
     *  verifiable linkage between this object and some object owner data
     */
    public byte[] outsideInfo;
    
    /** PCR that will be used in creation data  */
    public TPMS_PCR_SELECTION[] creationPCR;
    
    public TPM2_Create_REQUEST() { parentHandle = new TPM_HANDLE(); }
    
    /** @param _parentHandle Handle of parent for new object
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _inSensitive The sensitive data
     *  @param _inPublic The public template
     *  @param _outsideInfo Data that will be included in the creation data for this object to
     *         provide permanent, verifiable linkage between this object and some object owner
     *  data
     *  @param _creationPCR PCR that will be used in creation data
     */
    public TPM2_Create_REQUEST(TPM_HANDLE _parentHandle, TPMS_SENSITIVE_CREATE _inSensitive, TPMT_PUBLIC _inPublic, byte[] _outsideInfo, TPMS_PCR_SELECTION[] _creationPCR)
    {
        parentHandle = _parentHandle;
        inSensitive = _inSensitive;
        inPublic = _inPublic;
        outsideInfo = _outsideInfo;
        creationPCR = _creationPCR;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(inSensitive);
        buf.writeSizedObj(inPublic);
        buf.writeSizedByteBuf(outsideInfo);
        buf.writeObjArr(creationPCR);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        inSensitive = buf.createSizedObj(TPMS_SENSITIVE_CREATE.class);
        inPublic = buf.createSizedObj(TPMT_PUBLIC.class);
        outsideInfo = buf.readSizedByteBuf();
        creationPCR = buf.readObjArr(TPMS_PCR_SELECTION.class);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_Create_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Create_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Create_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_Create_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Create_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Create_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "parentHandle", parentHandle);
        _p.add(d, "TPMS_SENSITIVE_CREATE", "inSensitive", inSensitive);
        _p.add(d, "TPMT_PUBLIC", "inPublic", inPublic);
        _p.add(d, "byte", "outsideInfo", outsideInfo);
        _p.add(d, "TPMS_PCR_SELECTION", "creationPCR", creationPCR);
    }
}

//<<<
