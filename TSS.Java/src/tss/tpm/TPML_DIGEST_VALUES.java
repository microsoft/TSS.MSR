package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This list is used to convey a list of digest values. This type is returned by
 *  TPM2_PCR_Event() and TPM2_EventSequenceComplete() and is an input for TPM2_PCR_Extend().
 */
public class TPML_DIGEST_VALUES extends TpmStructure
{
    /** A list of tagged digests */
    public TPMT_HA[] digests;

    public TPML_DIGEST_VALUES() {}

    /** @param _digests A list of tagged digests */
    public TPML_DIGEST_VALUES(TPMT_HA[] _digests) { digests = _digests; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(digests); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { digests = buf.readObjArr(TPMT_HA.class); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_DIGEST_VALUES fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_DIGEST_VALUES.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_DIGEST_VALUES fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_DIGEST_VALUES fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_DIGEST_VALUES.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_DIGEST_VALUES");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_HA[]", "digests", digests);
    }
}

//<<<
