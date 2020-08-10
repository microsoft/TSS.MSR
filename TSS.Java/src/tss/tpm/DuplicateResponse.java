package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command duplicates a loaded object so that it may be used in a different
 *  hierarchy. The new parent key for the duplicate may be on the same or different TPM or
 *  TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
 */
public class DuplicateResponse extends RespStructure
{
    /** If the caller provided an encryption key or if symmetricAlg was TPM_ALG_NULL, then
     *  this will be the Empty Buffer; otherwise, it shall contain the TPM-generated,
     *  symmetric encryption key for the inner wrapper.
     */
    public byte[] encryptionKeyOut;
    
    /** Private area that may be encrypted by encryptionKeyIn; and may be doubly encrypted  */
    public TPM2B_PRIVATE duplicate;
    
    /** Seed protected by the asymmetric algorithms of new parent (NP)  */
    public byte[] outSymSeed;
    
    public DuplicateResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(encryptionKeyOut);
        duplicate.toTpm(buf);
        buf.writeSizedByteBuf(outSymSeed);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        encryptionKeyOut = buf.readSizedByteBuf();
        duplicate = TPM2B_PRIVATE.fromTpm(buf);
        outSymSeed = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static DuplicateResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(DuplicateResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static DuplicateResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static DuplicateResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(DuplicateResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("DuplicateResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "encryptionKeyOut", encryptionKeyOut);
        _p.add(d, "TPM2B_PRIVATE", "duplicate", duplicate);
        _p.add(d, "byte[]", "outSymSeed", outSymSeed);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
