package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used in TPM2_GetCapability() to return the policy associated with a
 *  permanent handle.
 */
public class TPMS_TAGGED_POLICY extends TpmStructure
{
    /** A permanent handle  */
    public TPM_HANDLE handle;

    /** The policy algorithm and hash  */
    public TPMT_HA policyHash;

    public TPMS_TAGGED_POLICY() { handle = new TPM_HANDLE(); }

    /** @param _handle A permanent handle
     *  @param _policyHash The policy algorithm and hash
     */
    public TPMS_TAGGED_POLICY(TPM_HANDLE _handle, TPMT_HA _policyHash)
    {
        handle = _handle;
        policyHash = _policyHash;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        handle.toTpm(buf);
        policyHash.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        policyHash = TPMT_HA.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_TAGGED_POLICY fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_TAGGED_POLICY.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_TAGGED_POLICY fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_TAGGED_POLICY fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_TAGGED_POLICY.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_TAGGED_POLICY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "TPMT_HA", "policyHash", policyHash);
    }
}

//<<<
