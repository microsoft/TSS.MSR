package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 83 Definition of Types for TPM2B_NONCE  */
public class TPM2B_NONCE extends TPM2B_DIGEST
{
    public TPM2B_NONCE() {}

    /** @param _buffer The buffer area that can be no larger than a digest  */
    public TPM2B_NONCE(byte[] _buffer) { super(_buffer); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2B_NONCE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_NONCE.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_NONCE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2B_NONCE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_NONCE.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_NONCE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<
