package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Command header [TSS]  */
public class CommandHeader extends TpmStructure
{
    /** Command tag (sessions, or no sessions)  */
    public TPM_ST Tag;

    /** Total command buffer length  */
    public int CommandSize;

    /** Command code  */
    public TPM_CC CommandCode;

    public CommandHeader() {}

    /** @param _Tag Command tag (sessions, or no sessions)
     *  @param _CommandSize Total command buffer length
     *  @param _CommandCode Command code
     */
    public CommandHeader(TPM_ST _Tag, int _CommandSize, TPM_CC _CommandCode)
    {
        Tag = _Tag;
        CommandSize = _CommandSize;
        CommandCode = _CommandCode;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        Tag.toTpm(buf);
        buf.writeInt(CommandSize);
        CommandCode.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        Tag = TPM_ST.fromTpm(buf);
        CommandSize = buf.readInt();
        CommandCode = TPM_CC.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static CommandHeader fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(CommandHeader.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static CommandHeader fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static CommandHeader fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(CommandHeader.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("CommandHeader");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ST", "Tag", Tag);
        _p.add(d, "int", "CommandSize", CommandSize);
        _p.add(d, "TPM_CC", "CommandCode", CommandCode);
    }
}

//<<<
