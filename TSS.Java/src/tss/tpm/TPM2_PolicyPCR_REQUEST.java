package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command is used to cause conditional gating of a policy based on PCR. This command
 *  together with TPM2_PolicyOR() allows one group of authorizations to occur when PCR are in
 *  one state and a different set of authorizations when the PCR are in a different state.
 */
public class TPM2_PolicyPCR_REQUEST extends TpmStructure
{
    /**
     *  handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /**
     *  expected digest value of the selected PCR using the hash algorithm of the
     *  session; may be zero length
     */
    public byte[] pcrDigest;
    
    /** the PCR to include in the check digest */
    public TPMS_PCR_SELECTION[] pcrs;
    
    public TPM2_PolicyPCR_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /**
     *  @param _policySession handle for the policy session being extended
     *         Auth Index: None
     *  @param _pcrDigest expected digest value of the selected PCR using the hash algorithm of the
     *         session; may be zero length
     *  @param _pcrs the PCR to include in the check digest
     */
    public TPM2_PolicyPCR_REQUEST(TPM_HANDLE _policySession, byte[] _pcrDigest, TPMS_PCR_SELECTION[] _pcrs)
    {
        policySession = _policySession;
        pcrDigest = _pcrDigest;
        pcrs = _pcrs;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        buf.writeSizedByteBuf(pcrDigest);
        buf.writeObjArr(pcrs);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        int _pcrDigestSize = buf.readShort() & 0xFFFF;
        pcrDigest = new byte[_pcrDigestSize];
        buf.readArrayOfInts(pcrDigest, 1, _pcrDigestSize);
        int _pcrsCount = buf.readInt();
        pcrs = new TPMS_PCR_SELECTION[_pcrsCount];
        for (int j=0; j < _pcrsCount; j++) pcrs[j] = new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(pcrs, _pcrsCount);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_PolicyPCR_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyPCR_REQUEST ret = new TPM2_PolicyPCR_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_PolicyPCR_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyPCR_REQUEST ret = new TPM2_PolicyPCR_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyPCR_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "pcrDigest", pcrDigest);
        _p.add(d, "TPMS_PCR_SELECTION", "pcrs", pcrs);
    }
}

//<<<
