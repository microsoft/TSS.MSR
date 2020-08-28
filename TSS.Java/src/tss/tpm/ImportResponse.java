package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows an object to be encrypted using the symmetric encryption values of
 *  a Storage Key. After encryption, the object may be loaded and used in the new
 *  hierarchy. The imported object (duplicate) may be singly encrypted, multiply
 *  encrypted, or unencrypted.
 */
public class ImportResponse extends RespStructure
{
    /** The sensitive area encrypted with the symmetric key of parentHandle  */
    public TPM2B_PRIVATE outPrivate;

    public ImportResponse() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { outPrivate.toTpm(buf); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { outPrivate = TPM2B_PRIVATE.fromTpm(buf); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static ImportResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ImportResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ImportResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static ImportResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ImportResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("ImportResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM2B_PRIVATE", "outPrivate", outPrivate);
    }
}

//<<<
