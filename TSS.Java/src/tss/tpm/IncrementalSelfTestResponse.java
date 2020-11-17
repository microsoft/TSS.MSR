package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command causes the TPM to perform a test of the selected algorithms. */
public class IncrementalSelfTestResponse extends RespStructure
{
    /** List of algorithms that need testing */
    public TPM_ALG_ID[] toDoList;

    public IncrementalSelfTestResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(toDoList); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { toDoList = buf.readObjArr(TPM_ALG_ID.class); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static IncrementalSelfTestResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(IncrementalSelfTestResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static IncrementalSelfTestResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static IncrementalSelfTestResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(IncrementalSelfTestResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("IncrementalSelfTestResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID[]", "toDoList", toDoList);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(4, 2); }
}

//<<<
