package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to create a Primary Object under one of the Primary Seeds or a
 *  Temporary Object under TPM_RH_NULL. The command uses a TPM2B_PUBLIC as a template for
 *  the object to be created. The size of the unique field shall not be checked for
 *  consistency with the other object parameters. The command will create and load a
 *  Primary Object. The sensitive area is not returned.
 */
public class TPM2_CreatePrimary_REQUEST extends ReqStructure
{
    /** TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE primaryHandle;

    /** The sensitive data, see TPM 2.0 Part 1 Sensitive Values  */
    public TPMS_SENSITIVE_CREATE inSensitive;

    /** The public template  */
    public TPMT_PUBLIC inPublic;

    /** Data that will be included in the creation data for this object to provide permanent,
     *  verifiable linkage between this object and some object owner data
     */
    public byte[] outsideInfo;

    /** PCR that will be used in creation data  */
    public TPMS_PCR_SELECTION[] creationPCR;

    public TPM2_CreatePrimary_REQUEST() { primaryHandle = new TPM_HANDLE(); }

    /** @param _primaryHandle TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _inSensitive The sensitive data, see TPM 2.0 Part 1 Sensitive Values
     *  @param _inPublic The public template
     *  @param _outsideInfo Data that will be included in the creation data for this object to
     *         provide permanent, verifiable linkage between this object and some object owner
     *  data
     *  @param _creationPCR PCR that will be used in creation data
     */
    public TPM2_CreatePrimary_REQUEST(TPM_HANDLE _primaryHandle, TPMS_SENSITIVE_CREATE _inSensitive, TPMT_PUBLIC _inPublic, byte[] _outsideInfo, TPMS_PCR_SELECTION[] _creationPCR)
    {
        primaryHandle = _primaryHandle;
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
    public static TPM2_CreatePrimary_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_CreatePrimary_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_CreatePrimary_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_CreatePrimary_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_CreatePrimary_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CreatePrimary_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "primaryHandle", primaryHandle);
        _p.add(d, "TPMS_SENSITIVE_CREATE", "inSensitive", inSensitive);
        _p.add(d, "TPMT_PUBLIC", "inPublic", inPublic);
        _p.add(d, "byte[]", "outsideInfo", outsideInfo);
        _p.add(d, "TPMS_PCR_SELECTION[]", "creationPCR", creationPCR);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {primaryHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
