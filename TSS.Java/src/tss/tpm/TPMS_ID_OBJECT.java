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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(integrityHMAC);
        buf.writeByteBuf(encIdentity);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _integrityHMACSize = buf.readShort() & 0xFFFF;
        integrityHMAC = new byte[_integrityHMACSize];
        buf.readArrayOfInts(integrityHMAC, 1, _integrityHMACSize);
        InByteBuf.SizedStructInfo si = buf.structSize.peek();
        int _encIdentitySize = si.Size - (buf.curPos() - si.StartPos);
        encIdentity = new byte[_encIdentitySize];
        buf.readArrayOfInts(encIdentity, 1, _encIdentitySize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMS_ID_OBJECT fromBytes (byte[] byteBuf) 
    {
        TPMS_ID_OBJECT ret = new TPMS_ID_OBJECT();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_ID_OBJECT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPMS_ID_OBJECT fromTpm (InByteBuf buf) 
    {
        TPMS_ID_OBJECT ret = new TPMS_ID_OBJECT();
        ret.initFromTpm(buf);
        return ret;
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
        _p.add(d, "byte", "integrityHMAC", integrityHMAC);
        _p.add(d, "byte", "encIdentity", encIdentity);
    }
}

//<<<
