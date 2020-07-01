package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs ECC encryption as described in Part 1, Annex D.  */
public class ECC_EncryptResponse extends RespStructure
{
    /** The public ephemeral key used for ECDH  */
    public TPMS_ECC_POINT C1;
    
    /** The data block produced by the XOR process  */
    public byte[] C2;
    
    /** The integrity value  */
    public byte[] C3;
    
    public ECC_EncryptResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(C1);
        buf.writeSizedByteBuf(C2);
        buf.writeSizedByteBuf(C3);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        C1 = buf.createSizedObj(TPMS_ECC_POINT.class);
        C2 = buf.readSizedByteBuf();
        C3 = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static ECC_EncryptResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ECC_EncryptResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ECC_EncryptResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static ECC_EncryptResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ECC_EncryptResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECC_Encrypt_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "C1", C1);
        _p.add(d, "byte", "C2", C2);
        _p.add(d, "byte", "C3", C3);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
