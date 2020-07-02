package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to set the time remaining before an Authenticated Countdown Timer
 *  (ACT) expires.
 */
public class TPM2_ACT_SetTimeout_REQUEST extends ReqStructure
{
    /** Handle of the selected ACT
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE actHandle;
    
    /** The start timeout value for the ACT in seconds  */
    public int startTimeout;
    
    public TPM2_ACT_SetTimeout_REQUEST() { actHandle = new TPM_HANDLE(); }
    
    /** @param _actHandle Handle of the selected ACT
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _startTimeout The start timeout value for the ACT in seconds
     */
    public TPM2_ACT_SetTimeout_REQUEST(TPM_HANDLE _actHandle, int _startTimeout)
    {
        actHandle = _actHandle;
        startTimeout = _startTimeout;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeInt(startTimeout); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { startTimeout = buf.readInt(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_ACT_SetTimeout_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ACT_SetTimeout_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ACT_SetTimeout_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_ACT_SetTimeout_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ACT_SetTimeout_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ACT_SetTimeout_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "actHandle", actHandle);
        _p.add(d, "int", "startTimeout", startTimeout);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {actHandle}; }
}

//<<<
