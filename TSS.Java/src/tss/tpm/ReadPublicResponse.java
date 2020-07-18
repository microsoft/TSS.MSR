package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows access to the public area of a loaded object.  */
public class ReadPublicResponse extends RespStructure
{
    /** Structure containing the public area of an object  */
    public TPMT_PUBLIC outPublic;
    
    /** Name of the object  */
    public byte[] name;
    
    /** The Qualified Name of the object  */
    public byte[] qualifiedName;
    
    public ReadPublicResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(outPublic);
        buf.writeSizedByteBuf(name);
        buf.writeSizedByteBuf(qualifiedName);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        outPublic = buf.createSizedObj(TPMT_PUBLIC.class);
        name = buf.readSizedByteBuf();
        qualifiedName = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static ReadPublicResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ReadPublicResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ReadPublicResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static ReadPublicResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ReadPublicResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("ReadPublicResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_PUBLIC", "outPublic", outPublic);
        _p.add(d, "byte", "name", name);
        _p.add(d, "byte", "qualifiedName", qualifiedName);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
