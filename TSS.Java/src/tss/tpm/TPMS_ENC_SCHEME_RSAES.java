package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  These are the RSA encryption schemes that only need a hash algorithm as
 *  a controlling parameter.
 */
public class TPMS_ENC_SCHEME_RSAES extends TPMS_EMPTY
{
    public TPMS_ENC_SCHEME_RSAES() {}
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ENC_SCHEME_RSAES");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

