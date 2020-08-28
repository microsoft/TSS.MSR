package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This sized buffer to contain the signed structure. The attestationData is the signed
 *  portion of the structure. The size parameter is not signed.
 */
public class TPM2B_ATTEST extends TpmStructure
{
    /** The signed structure  */
    public TPMS_ATTEST attestationData;

    public TPM2B_ATTEST() {}

    /** @param _attestationData The signed structure  */
    public TPM2B_ATTEST(TPMS_ATTEST _attestationData) { attestationData = _attestationData; }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(attestationData); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { attestationData = buf.createSizedObj(TPMS_ATTEST.class); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2B_ATTEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_ATTEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_ATTEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2B_ATTEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_ATTEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_ATTEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ATTEST", "attestationData", attestationData);
    }
}

//<<<
