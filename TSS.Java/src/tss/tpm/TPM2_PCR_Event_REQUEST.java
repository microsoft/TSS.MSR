package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to cause an update to the indicated PCR.  */
public class TPM2_PCR_Event_REQUEST extends ReqStructure
{
    /** Handle of the PCR
     *  Auth Handle: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE pcrHandle;

    /** Event data in sized buffer  */
    public byte[] eventData;

    public TPM2_PCR_Event_REQUEST() { pcrHandle = new TPM_HANDLE(); }

    /** @param _pcrHandle Handle of the PCR
     *         Auth Handle: 1
     *         Auth Role: USER
     *  @param _eventData Event data in sized buffer
     */
    public TPM2_PCR_Event_REQUEST(TPM_HANDLE _pcrHandle, byte[] _eventData)
    {
        pcrHandle = _pcrHandle;
        eventData = _eventData;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(eventData); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { eventData = buf.readSizedByteBuf(); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_PCR_Event_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PCR_Event_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PCR_Event_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_PCR_Event_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PCR_Event_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Event_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "pcrHandle", pcrHandle);
        _p.add(d, "byte[]", "eventData", eventData);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {pcrHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
