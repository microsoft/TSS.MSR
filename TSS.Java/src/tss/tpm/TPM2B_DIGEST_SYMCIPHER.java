package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Auto-derived from TPM2B_DIGEST to provide unique GetUnionSelector() implementation */
public class TPM2B_DIGEST_SYMCIPHER extends TPM2B_DIGEST
{
    public TPM2B_DIGEST_SYMCIPHER() {}

    /** @param _buffer The buffer area that can be no larger than a digest */
    public TPM2B_DIGEST_SYMCIPHER(byte[] _buffer) { super(_buffer); }

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.SYMCIPHER; }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_DIGEST_SYMCIPHER fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_DIGEST_SYMCIPHER.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_DIGEST_SYMCIPHER fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_DIGEST_SYMCIPHER fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_DIGEST_SYMCIPHER.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_DIGEST_SYMCIPHER");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<
