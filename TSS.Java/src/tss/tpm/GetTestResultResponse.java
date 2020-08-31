package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns manufacturer-specific information regarding the results of a
 *  self-test and an indication of the test status.
 */
public class GetTestResultResponse extends RespStructure
{
    /** Test result data
     *  contains manufacturer-specific information
     */
    public byte[] outData;
    public TPM_RC testResult;

    public GetTestResultResponse() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(outData);
        testResult.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        outData = buf.readSizedByteBuf();
        testResult = TPM_RC.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static GetTestResultResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(GetTestResultResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static GetTestResultResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static GetTestResultResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(GetTestResultResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("GetTestResultResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "outData", outData);
        _p.add(d, "TPM_RC", "testResult", testResult);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
