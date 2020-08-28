package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.  */
public class EC_EphemeralResponse extends RespStructure
{
    /** Ephemeral public key Q [r]G  */
    public TPMS_ECC_POINT Q;

    /** Least-significant 16 bits of commitCount  */
    public int counter;

    public EC_EphemeralResponse() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(Q);
        buf.writeShort(counter);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        Q = buf.createSizedObj(TPMS_ECC_POINT.class);
        counter = buf.readShort();
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static EC_EphemeralResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(EC_EphemeralResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static EC_EphemeralResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static EC_EphemeralResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(EC_EphemeralResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("EC_EphemeralResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "Q", Q);
        _p.add(d, "int", "counter", counter);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
