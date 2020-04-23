package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command returns manufacturer-specific information regarding the results of a self-test and
 *  an indication of the test status.
 */
public class TPM2_GetTestResult_REQUEST extends TpmStructure
{
    public TPM2_GetTestResult_REQUEST() {}
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetTestResult_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

