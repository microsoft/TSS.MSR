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
public class CreatePrimaryResponse extends RespStructure
{
    /** Handle of type TPM_HT_TRANSIENT for created Primary Object  */
    public TPM_HANDLE handle;
    
    /** The public portion of the created object  */
    public TPMT_PUBLIC outPublic;
    
    /** Contains a TPMT_CREATION_DATA  */
    public TPMS_CREATION_DATA creationData;
    
    /** Digest of creationData using nameAlg of outPublic  */
    public byte[] creationHash;
    
    /** Ticket used by TPM2_CertifyCreation() to validate that the creation data was produced
     *  by the TPM
     */
    public TPMT_TK_CREATION creationTicket;
    
    /** The name of the created object  */
    public byte[] name;
    
    public CreatePrimaryResponse() { handle = new TPM_HANDLE(); }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(outPublic);
        buf.writeSizedObj(creationData);
        buf.writeSizedByteBuf(creationHash);
        creationTicket.toTpm(buf);
        buf.writeSizedByteBuf(name);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        outPublic = buf.createSizedObj(TPMT_PUBLIC.class);
        creationData = buf.createSizedObj(TPMS_CREATION_DATA.class);
        creationHash = buf.readSizedByteBuf();
        creationTicket = TPMT_TK_CREATION.fromTpm(buf);
        name = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static CreatePrimaryResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(CreatePrimaryResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static CreatePrimaryResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static CreatePrimaryResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(CreatePrimaryResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("CreatePrimaryResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "TPMT_PUBLIC", "outPublic", outPublic);
        _p.add(d, "TPMS_CREATION_DATA", "creationData", creationData);
        _p.add(d, "byte", "creationHash", creationHash);
        _p.add(d, "TPMT_TK_CREATION", "creationTicket", creationTicket);
        _p.add(d, "byte", "name", name);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public TPM_HANDLE getHandle() { return handle; }

    @Override
    public void setHandle(TPM_HANDLE h) { handle = h; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
