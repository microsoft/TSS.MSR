package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 185 Definition of {RSA} TPMS_SIGNATURE_RSA Structure */
public class TPMS_SIGNATURE_RSASSA extends TPMS_SIGNATURE_RSA
{
    public TPMS_SIGNATURE_RSASSA() {}
    
    /**
     *  @param _hash the hash algorithm used to digest the message
     *         TPM_ALG_NULL is not allowed.
     *  @param _sig The signature is the size of a public key.
     */
    public TPMS_SIGNATURE_RSASSA(TPM_ALG_ID _hash, byte[] _sig)
    {
        super(_hash, _sig);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIGNATURE_RSASSA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

