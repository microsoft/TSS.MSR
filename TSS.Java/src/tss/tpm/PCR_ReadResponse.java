package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the values of all PCR specified in pcrSelectionIn.  */
public class PCR_ReadResponse extends RespStructure
{
    /** The current value of the PCR update counter  */
    public int pcrUpdateCounter;

    /** The PCR in the returned list  */
    public TPMS_PCR_SELECTION[] pcrSelectionOut;

    /** The contents of the PCR indicated in pcrSelectOut-˃ pcrSelection[] as tagged digests  */
    public TPM2B_DIGEST[] pcrValues;

    public PCR_ReadResponse() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeInt(pcrUpdateCounter);
        buf.writeObjArr(pcrSelectionOut);
        buf.writeObjArr(pcrValues);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        pcrUpdateCounter = buf.readInt();
        pcrSelectionOut = buf.readObjArr(TPMS_PCR_SELECTION.class);
        pcrValues = buf.readObjArr(TPM2B_DIGEST.class);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static PCR_ReadResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(PCR_ReadResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static PCR_ReadResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static PCR_ReadResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(PCR_ReadResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("PCR_ReadResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "int", "pcrUpdateCounter", pcrUpdateCounter);
        _p.add(d, "TPMS_PCR_SELECTION[]", "pcrSelectionOut", pcrSelectionOut);
        _p.add(d, "TPM2B_DIGEST[]", "pcrValues", pcrValues);
    }
}

//<<<
