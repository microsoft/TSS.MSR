package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to prepare the TPM for a power cycle. The shutdownType parameter
 *  indicates how the subsequent TPM2_Startup() will be processed.
 */
public class TPM2_Shutdown_REQUEST extends TpmStructure
{
    /** TPM_SU_CLEAR or TPM_SU_STATE  */
    public TPM_SU shutdownType;
    
    public TPM2_Shutdown_REQUEST() {}
    
    /** @param _shutdownType TPM_SU_CLEAR or TPM_SU_STATE  */
    public TPM2_Shutdown_REQUEST(TPM_SU _shutdownType) { shutdownType = _shutdownType; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { shutdownType.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { shutdownType = TPM_SU.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_Shutdown_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Shutdown_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Shutdown_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_Shutdown_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Shutdown_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Shutdown_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_SU", "shutdownType", shutdownType);
    }
}

//<<<
