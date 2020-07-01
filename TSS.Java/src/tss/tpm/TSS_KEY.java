package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Contains the public and private part of a TPM key  */
public class TSS_KEY extends TpmStructure
{
    /** Public part of key  */
    public TPMT_PUBLIC publicPart;
    
    /** Private part is the encrypted sensitive part of key  */
    public byte[] privatePart;
    
    public TSS_KEY() {}
    
    /** @param _publicPart Public part of key
     *  @param _privatePart Private part is the encrypted sensitive part of key
     */
    public TSS_KEY(TPMT_PUBLIC _publicPart, byte[] _privatePart)
    {
        publicPart = _publicPart;
        privatePart = _privatePart;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        publicPart.toTpm(buf);
        buf.writeSizedByteBuf(privatePart);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        publicPart = TPMT_PUBLIC.fromTpm(buf);
        privatePart = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TSS_KEY fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TSS_KEY.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TSS_KEY fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TSS_KEY fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TSS_KEY.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TSS_KEY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_PUBLIC", "publicPart", publicPart);
        _p.add(d, "byte", "privatePart", privatePart);
    }
}

//<<<
