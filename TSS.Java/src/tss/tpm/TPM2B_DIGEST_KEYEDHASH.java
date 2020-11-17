package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Auto-derived from TPM2B_DIGEST */
public class TPM2B_DIGEST_KEYEDHASH extends TPM2B_DIGEST
{
    public TPM2B_DIGEST_KEYEDHASH() {}

    /** @param _buffer The buffer area that can be no larger than a digest */
    public TPM2B_DIGEST_KEYEDHASH(byte[] _buffer) { super(_buffer); }

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.KEYEDHASH; }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_DIGEST_KEYEDHASH fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_DIGEST_KEYEDHASH.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_DIGEST_KEYEDHASH fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_DIGEST_KEYEDHASH fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_DIGEST_KEYEDHASH.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_DIGEST_KEYEDHASH");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<
