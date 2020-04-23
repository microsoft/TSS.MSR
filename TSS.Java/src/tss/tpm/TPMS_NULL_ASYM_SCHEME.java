package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  Custom data structure representing an empty element (i.e. the one with 
 *  no data to marshal) for selector algorithm TPM_ALG_NULL for the union TPMU_ASYM_SCHEME
 */
public class TPMS_NULL_ASYM_SCHEME extends TPMS_NULL_UNION
{
    public TPMS_NULL_ASYM_SCHEME() {}
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NULL_ASYM_SCHEME");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

