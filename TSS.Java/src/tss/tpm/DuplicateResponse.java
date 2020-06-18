package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command duplicates a loaded object so that it may be used in a different
 *  hierarchy. The new parent key for the duplicate may be on the same or different TPM or
 *  TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
 */
public class DuplicateResponse extends TpmStructure
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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(encryptionKeyOut);
        duplicate.toTpm(buf);
        buf.writeSizedByteBuf(outSymSeed);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _encryptionKeyOutSize = buf.readShort() & 0xFFFF;
        encryptionKeyOut = new byte[_encryptionKeyOutSize];
        buf.readArrayOfInts(encryptionKeyOut, 1, _encryptionKeyOutSize);
        duplicate = TPM2B_PRIVATE.fromTpm(buf);
        int _outSymSeedSize = buf.readShort() & 0xFFFF;
        outSymSeed = new byte[_outSymSeedSize];
        buf.readArrayOfInts(outSymSeed, 1, _outSymSeedSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static DuplicateResponse fromBytes (byte[] byteBuf) 
    {
        DuplicateResponse ret = new DuplicateResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static DuplicateResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static DuplicateResponse fromTpm (InByteBuf buf) 
    {
        DuplicateResponse ret = new DuplicateResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Duplicate_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "encryptionKeyOut", encryptionKeyOut);
        _p.add(d, "TPM2B_PRIVATE", "duplicate", duplicate);
        _p.add(d, "byte", "outSymSeed", outSymSeed);
    }
}

//<<<
