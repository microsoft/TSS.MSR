package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The purpose of this command is to send (copy) a loaded object from the TPM to an
 *  Attached Component.
 */
public class AC_SendResponse extends RespStructure
{
    /** May include AC specific data or information about an error.  */
    public TPMS_AC_OUTPUT acDataOut;

    public AC_SendResponse() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { acDataOut.toTpm(buf); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { acDataOut = TPMS_AC_OUTPUT.fromTpm(buf); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static AC_SendResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(AC_SendResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static AC_SendResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static AC_SendResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(AC_SendResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("AC_SendResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_AC_OUTPUT", "acDataOut", acDataOut);
    }
}

//<<<
