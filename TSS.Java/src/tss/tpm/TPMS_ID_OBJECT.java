package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used for sizing the TPM2B_ID_OBJECT.  */
public class TPMS_ID_OBJECT extends TpmStructure
{
    /** HMAC using the nameAlg of the storage key on the target TPM  */
    public byte[] integrityHMAC;

    /** Credential protector information returned if name matches the referenced object
     *  All of the encIdentity is encrypted, including the size field.
     *  NOTE The TPM is not required to check that the size is not larger than the digest of
     *  the nameAlg. However, if the size is larger, the ID object may not be usable on a TPM
     *  that has no digest larger than produced by nameAlg.
     */
    public byte[] encIdentity;

    public TPMS_ID_OBJECT() {}

    /** @param _integrityHMAC HMAC using the nameAlg of the storage key on the target TPM
     *  @param _encIdentity Credential protector information returned if name matches the
     *         referenced object
     *         All of the encIdentity is encrypted, including the size field.
     *         NOTE The TPM is not required to check that the size is not larger than the digest
     *         of the nameAlg. However, if the size is larger, the ID object may not be usable
     *  on
     *         a TPM that has no digest larger than produced by nameAlg.
     */
    public TPMS_ID_OBJECT(byte[] _integrityHMAC, byte[] _encIdentity)
    {
        integrityHMAC = _integrityHMAC;
        encIdentity = _encIdentity;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(integrityHMAC);
        buf.writeByteBuf(encIdentity);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        integrityHMAC = buf.readSizedByteBuf();
        encIdentity = buf.readByteBuf(buf.getCurStuctRemainingSize());
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_ID_OBJECT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_ID_OBJECT.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_ID_OBJECT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_ID_OBJECT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_ID_OBJECT.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ID_OBJECT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "integrityHMAC", integrityHMAC);
        _p.add(d, "byte[]", "encIdentity", encIdentity);
    }
}

//<<<
