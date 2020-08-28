package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is a placeholder to allow testing of the dispatch code.  */
public class TPM2_Vendor_TCG_Test_REQUEST extends ReqStructure
{
    /** Dummy data  */
    public byte[] inputData;

    public TPM2_Vendor_TCG_Test_REQUEST() {}

    /** @param _inputData Dummy data  */
    public TPM2_Vendor_TCG_Test_REQUEST(byte[] _inputData) { inputData = _inputData; }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(inputData); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { inputData = buf.readSizedByteBuf(); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_Vendor_TCG_Test_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Vendor_TCG_Test_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Vendor_TCG_Test_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_Vendor_TCG_Test_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Vendor_TCG_Test_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Vendor_TCG_Test_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "inputData", inputData);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
