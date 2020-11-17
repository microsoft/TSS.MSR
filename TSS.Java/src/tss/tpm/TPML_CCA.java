package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This list is only used in TPM2_GetCapability(capability = TPM_CAP_COMMANDS). */
public class TPML_CCA extends TpmStructure implements TPMU_CAPABILITIES
{
    /** A list of command codes attributes */
    public TPMA_CC[] commandAttributes;

    public TPML_CCA() {}

    /** @param _commandAttributes A list of command codes attributes */
    public TPML_CCA(TPMA_CC[] _commandAttributes) { commandAttributes = _commandAttributes; }

    /** TpmUnion method */
    public TPM_CAP GetUnionSelector() { return TPM_CAP.COMMANDS; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(commandAttributes); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { commandAttributes = buf.readObjArr(TPMA_CC.class); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_CCA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_CCA.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_CCA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_CCA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_CCA.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_CCA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMA_CC[]", "commandAttributes", commandAttributes);
    }
}

//<<<
