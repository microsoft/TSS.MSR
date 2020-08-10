package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** A list of command codes may be input to the TPM or returned by the TPM depending on
 *  the command.
 */
public class TPML_CC extends TpmStructure implements TPMU_CAPABILITIES
{
    /** A list of command codes
     *  The maximum only applies to a command code list in a command. The response size is
     *  limited only by the size of the parameter buffer.
     */
    public TPM_CC[] commandCodes;
    
    public TPML_CC() {}
    
    /** @param _commandCodes A list of command codes
     *         The maximum only applies to a command code list in a command. The response size
     *  is
     *         limited only by the size of the parameter buffer.
     */
    public TPML_CC(TPM_CC[] _commandCodes) { commandCodes = _commandCodes; }
    
    /** TpmUnion method  */
    public TPM_CAP GetUnionSelector() { return TPM_CAP.PP_COMMANDS; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(commandCodes); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { commandCodes = buf.readObjArr(TPM_CC.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPML_CC fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_CC.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPML_CC fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPML_CC fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_CC.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_CC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_CC[]", "commandCodes", commandCodes);
    }
}

//<<<
