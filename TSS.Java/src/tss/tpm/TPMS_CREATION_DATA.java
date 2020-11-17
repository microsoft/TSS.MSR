package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure provides information relating to the creation environment for the
 *  object. The creation data includes the parent Name, parent Qualified Name, and the
 *  digest of selected PCR. These values represent the environment in which the object was
 *  created. Creation data allows a relying party to determine if an object was created
 *  when some appropriate protections were present.
 */
public class TPMS_CREATION_DATA extends TpmStructure
{
    /** List indicating the PCR included in pcrDigest */
    public TPMS_PCR_SELECTION[] pcrSelect;

    /** Digest of the selected PCR using nameAlg of the object for which this structure is
     *  being created
     *  pcrDigest.size shall be zero if the pcrSelect list is empty.
     */
    public byte[] pcrDigest;

    /** The locality at which the object was created */
    public TPMA_LOCALITY locality;

    /** NameAlg of the parent */
    public TPM_ALG_ID parentNameAlg;

    /** Name of the parent at time of creation
     *  The size will match digest size associated with parentNameAlg unless it is
     *  TPM_ALG_NULL, in which case the size will be 4 and parentName will be the hierarchy handle.
     */
    public byte[] parentName;

    /** Qualified Name of the parent at the time of creation
     *  Size is the same as parentName.
     */
    public byte[] parentQualifiedName;

    /** Association with additional information added by the key creator
     *  This will be the contents of the outsideInfo parameter in TPM2_Create() or TPM2_CreatePrimary().
     */
    public byte[] outsideInfo;

    public TPMS_CREATION_DATA() { parentNameAlg = TPM_ALG_ID.NULL; }

    /** @param _pcrSelect List indicating the PCR included in pcrDigest
     *  @param _pcrDigest Digest of the selected PCR using nameAlg of the object for which this
     *         structure is being created
     *         pcrDigest.size shall be zero if the pcrSelect list is empty.
     *  @param _locality The locality at which the object was created
     *  @param _parentNameAlg NameAlg of the parent
     *  @param _parentName Name of the parent at time of creation
     *         The size will match digest size associated with parentNameAlg unless it is
     *         TPM_ALG_NULL, in which case the size will be 4 and parentName will be the hierarchy
     *         handle.
     *  @param _parentQualifiedName Qualified Name of the parent at the time of creation
     *         Size is the same as parentName.
     *  @param _outsideInfo Association with additional information added by the key creator
     *         This will be the contents of the outsideInfo parameter in TPM2_Create() or
     *         TPM2_CreatePrimary().
     */
    public TPMS_CREATION_DATA(TPMS_PCR_SELECTION[] _pcrSelect, byte[] _pcrDigest, TPMA_LOCALITY _locality, TPM_ALG_ID _parentNameAlg, byte[] _parentName, byte[] _parentQualifiedName, byte[] _outsideInfo)
    {
        pcrSelect = _pcrSelect;
        pcrDigest = _pcrDigest;
        locality = _locality;
        parentNameAlg = _parentNameAlg;
        parentName = _parentName;
        parentQualifiedName = _parentQualifiedName;
        outsideInfo = _outsideInfo;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeObjArr(pcrSelect);
        buf.writeSizedByteBuf(pcrDigest);
        locality.toTpm(buf);
        parentNameAlg.toTpm(buf);
        buf.writeSizedByteBuf(parentName);
        buf.writeSizedByteBuf(parentQualifiedName);
        buf.writeSizedByteBuf(outsideInfo);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        pcrSelect = buf.readObjArr(TPMS_PCR_SELECTION.class);
        pcrDigest = buf.readSizedByteBuf();
        locality = TPMA_LOCALITY.fromTpm(buf);
        parentNameAlg = TPM_ALG_ID.fromTpm(buf);
        parentName = buf.readSizedByteBuf();
        parentQualifiedName = buf.readSizedByteBuf();
        outsideInfo = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_CREATION_DATA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_CREATION_DATA.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_CREATION_DATA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_CREATION_DATA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_CREATION_DATA.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CREATION_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_PCR_SELECTION[]", "pcrSelect", pcrSelect);
        _p.add(d, "byte[]", "pcrDigest", pcrDigest);
        _p.add(d, "TPMA_LOCALITY", "locality", locality);
        _p.add(d, "TPM_ALG_ID", "parentNameAlg", parentNameAlg);
        _p.add(d, "byte[]", "parentName", parentName);
        _p.add(d, "byte[]", "parentQualifiedName", parentQualifiedName);
        _p.add(d, "byte[]", "outsideInfo", outsideInfo);
    }
}

//<<<
