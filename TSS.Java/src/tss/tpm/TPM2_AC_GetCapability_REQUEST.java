package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The purpose of this command is to obtain information about an Attached Component
 *  referenced by an AC handle.
 */
public class TPM2_AC_GetCapability_REQUEST extends ReqStructure
{
    /** Handle indicating the Attached Component
     *  Auth Index: None
     */
    public TPM_HANDLE ac;
    
    /** Starting info type  */
    public TPM_AT capability;
    
    /** Maximum number of values to return  */
    public int count;
    
    public TPM2_AC_GetCapability_REQUEST() { ac = new TPM_HANDLE(); }
    
    /** @param _ac Handle indicating the Attached Component
     *         Auth Index: None
     *  @param _capability Starting info type
     *  @param _count Maximum number of values to return
     */
    public TPM2_AC_GetCapability_REQUEST(TPM_HANDLE _ac, TPM_AT _capability, int _count)
    {
        ac = _ac;
        capability = _capability;
        count = _count;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        capability.toTpm(buf);
        buf.writeInt(count);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        capability = TPM_AT.fromTpm(buf);
        count = buf.readInt();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_AC_GetCapability_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_AC_GetCapability_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_AC_GetCapability_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_AC_GetCapability_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_AC_GetCapability_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_AC_GetCapability_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "ac", ac);
        _p.add(d, "TPM_AT", "capability", capability);
        _p.add(d, "int", "count", count);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {ac}; }
}

//<<<
