package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** TPM2_Startup() is always preceded by _TPM_Init, which is the physical indication that
 *  TPM initialization is necessary because of a system-wide reset. TPM2_Startup() is only
 *  valid after _TPM_Init. Additional TPM2_Startup() commands are not allowed after it has
 *  completed successfully. If a TPM requires TPM2_Startup() and another command is
 *  received, or if the TPM receives TPM2_Startup() when it is not required, the TPM shall
 *  return TPM_RC_INITIALIZE.
 */
public class TPM2_Startup_REQUEST extends TpmStructure
{
    /** TPM_SU_CLEAR or TPM_SU_STATE  */
    public TPM_SU startupType;
    
    public TPM2_Startup_REQUEST() {}
    
    /** @param _startupType TPM_SU_CLEAR or TPM_SU_STATE  */
    public TPM2_Startup_REQUEST(TPM_SU _startupType) { startupType = _startupType; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { startupType.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { startupType = TPM_SU.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_Startup_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Startup_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Startup_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_Startup_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Startup_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Startup_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_SU", "startupType", startupType);
    }
}

//<<<
