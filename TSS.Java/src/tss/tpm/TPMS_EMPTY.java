package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This structure is used as a placeholder. In some cases, a union will have a selector value
 *  with no data to unmarshal when that type is selected. Rather than leave the entry
 *  empty, TPMS_EMPTY may be selected.
 */
public class TPMS_EMPTY extends TpmStructure implements TPMU_ASYM_SCHEME
{
    public TPMS_EMPTY() {}
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_EMPTY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

