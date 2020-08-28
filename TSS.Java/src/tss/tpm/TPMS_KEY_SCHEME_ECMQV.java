package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These are the ECC schemes that only need a hash algorithm as a controlling parameter.  */
public class TPMS_KEY_SCHEME_ECMQV extends TPMS_SCHEME_HASH
{
    public TPMS_KEY_SCHEME_ECMQV() {}

    /** @param _hashAlg The hash algorithm used to digest the message  */
    public TPMS_KEY_SCHEME_ECMQV(TPM_ALG_ID _hashAlg) { super(_hashAlg); }

    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ECMQV; }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_KEY_SCHEME_ECMQV fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_KEY_SCHEME_ECMQV.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_KEY_SCHEME_ECMQV fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_KEY_SCHEME_ECMQV fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_KEY_SCHEME_ECMQV.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_KEY_SCHEME_ECMQV");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<
