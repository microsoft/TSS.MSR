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
public class CreateResponse extends RespStructure
{
    /** The private portion of the object */
    public TPM2B_PRIVATE outPrivate;

    /** The public portion of the created object */
    public TPMT_PUBLIC outPublic;

    /** Contains a TPMS_CREATION_DATA */
    public TPMS_CREATION_DATA creationData;

    /** Digest of creationData using nameAlg of outPublic */
    public byte[] creationHash;

    /** Ticket used by TPM2_CertifyCreation() to validate that the creation data was produced
     *  by the TPM
     */
    public TPMT_TK_CREATION creationTicket;

    public CreateResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        outPrivate.toTpm(buf);
        buf.writeSizedObj(outPublic);
        buf.writeSizedObj(creationData);
        buf.writeSizedByteBuf(creationHash);
        creationTicket.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        outPrivate = TPM2B_PRIVATE.fromTpm(buf);
        outPublic = buf.createSizedObj(TPMT_PUBLIC.class);
        creationData = buf.createSizedObj(TPMS_CREATION_DATA.class);
        creationHash = buf.readSizedByteBuf();
        creationTicket = TPMT_TK_CREATION.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static CreateResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(CreateResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static CreateResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static CreateResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(CreateResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("CreateResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM2B_PRIVATE", "outPrivate", outPrivate);
        _p.add(d, "TPMT_PUBLIC", "outPublic", outPublic);
        _p.add(d, "TPMS_CREATION_DATA", "creationData", creationData);
        _p.add(d, "byte[]", "creationHash", creationHash);
        _p.add(d, "TPMT_TK_CREATION", "creationTicket", creationTicket);
    }
}

//<<<
