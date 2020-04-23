package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 187 Definition of {ECC} TPMS_SIGNATURE_ECC Structure */
public class TPMS_SIGNATURE_ECSCHNORR extends TPMS_SIGNATURE_ECC
{
    public TPMS_SIGNATURE_ECSCHNORR() {}
    
    /**
     *  @param _hash the hash algorithm used in the signature process
     *         TPM_ALG_NULL is not allowed.
     *  @param _signatureR TBD
     *  @param _signatureS TBD
     */
    public TPMS_SIGNATURE_ECSCHNORR(TPM_ALG_ID _hash, byte[] _signatureR, byte[] _signatureS)
    {
        super(_hash, _signatureR, _signatureS);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIGNATURE_ECSCHNORR");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

