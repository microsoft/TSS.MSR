package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is a placeholder to allow testing of the dispatch code.  */
public class Vendor_TCG_TestResponse extends RespStructure
{
    /** Dummy data  */
    public byte[] outputData;
    
    public Vendor_TCG_TestResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(outputData); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { outputData = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static Vendor_TCG_TestResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(Vendor_TCG_TestResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static Vendor_TCG_TestResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static Vendor_TCG_TestResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(Vendor_TCG_TestResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("Vendor_TCG_TestResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "outputData", outputData);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
