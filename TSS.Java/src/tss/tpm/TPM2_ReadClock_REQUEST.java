package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command reads the current TPMS_TIME_INFO structure that contains the current
 *  setting of Time, Clock, resetCount, and restartCount.
 */
public class TPM2_ReadClock_REQUEST extends TpmStructure
{
    public TPM2_ReadClock_REQUEST() {}
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ReadClock_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<
