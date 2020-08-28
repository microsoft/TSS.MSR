package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows the TPM to serve in the role as a Duplication Authority. If proper
 *  authorization for use of the oldParent is provided, then an HMAC key and a symmetric
 *  key are recovered from inSymSeed and used to integrity check and decrypt inDuplicate.
 *  A new protection seed value is generated according to the methods appropriate for
 *  newParent and the blob is re-encrypted and a new integrity value is computed. The
 *  re-encrypted blob is returned in outDuplicate and the symmetric key returned in outSymKey.
 */
public class RewrapResponse extends RespStructure
{
    /** An object encrypted using symmetric key derived from outSymSeed  */
    public TPM2B_PRIVATE outDuplicate;

    /** Seed for a symmetric key protected by newParent asymmetric key  */
    public byte[] outSymSeed;

    public RewrapResponse() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        outDuplicate.toTpm(buf);
        buf.writeSizedByteBuf(outSymSeed);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        outDuplicate = TPM2B_PRIVATE.fromTpm(buf);
        outSymSeed = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static RewrapResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(RewrapResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static RewrapResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static RewrapResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(RewrapResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("RewrapResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM2B_PRIVATE", "outDuplicate", outDuplicate);
        _p.add(d, "byte[]", "outSymSeed", outSymSeed);
    }
}

//<<<
