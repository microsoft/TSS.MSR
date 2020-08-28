package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to read a copy of the current firmware installed in the TPM.  */
public class TPM2_FirmwareRead_REQUEST extends ReqStructure
{
    /** The number of previous calls to this command in this sequence
     *  set to 0 on the first call
     */
    public int sequenceNumber;

    public TPM2_FirmwareRead_REQUEST() {}

    /** @param _sequenceNumber The number of previous calls to this command in this sequence
     *         set to 0 on the first call
     */
    public TPM2_FirmwareRead_REQUEST(int _sequenceNumber) { sequenceNumber = _sequenceNumber; }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeInt(sequenceNumber); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { sequenceNumber = buf.readInt(); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_FirmwareRead_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_FirmwareRead_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_FirmwareRead_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_FirmwareRead_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_FirmwareRead_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FirmwareRead_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "int", "sequenceNumber", sequenceNumber);
    }
}

//<<<
