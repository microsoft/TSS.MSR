package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs a hash operation on a data buffer and returns the results.  */
public class HashResponse extends RespStructure
{
    /** Results  */
    public byte[] outHash;
    
    /** Ticket indicating that the sequence of octets used to compute outDigest did not start
     *  with TPM_GENERATED_VALUE
     *  will be a NULL ticket if the digest may not be signed with a restricted key
     */
    public TPMT_TK_HASHCHECK validation;
    
    public HashResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(outHash);
        validation.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        outHash = buf.readSizedByteBuf();
        validation = TPMT_TK_HASHCHECK.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static HashResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(HashResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static HashResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static HashResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(HashResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("HashResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "outHash", outHash);
        _p.add(d, "TPMT_TK_HASHCHECK", "validation", validation);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
