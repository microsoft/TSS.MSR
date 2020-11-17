package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This list is used to indicate the PCR that are included in a selection when more than
 *  one PCR value may be selected.
 */
public class TPML_PCR_SELECTION extends TpmStructure implements TPMU_CAPABILITIES
{
    /** List of selections */
    public TPMS_PCR_SELECTION[] pcrSelections;

    public TPML_PCR_SELECTION() {}

    /** @param _pcrSelections List of selections */
    public TPML_PCR_SELECTION(TPMS_PCR_SELECTION[] _pcrSelections) { pcrSelections = _pcrSelections; }

    /** TpmUnion method */
    public TPM_CAP GetUnionSelector() { return TPM_CAP.PCRS; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(pcrSelections); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { pcrSelections = buf.readObjArr(TPMS_PCR_SELECTION.class); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_PCR_SELECTION fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_PCR_SELECTION.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_PCR_SELECTION fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_PCR_SELECTION fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_PCR_SELECTION.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_PCR_SELECTION");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_PCR_SELECTION[]", "pcrSelections", pcrSelections);
    }
}

//<<<
