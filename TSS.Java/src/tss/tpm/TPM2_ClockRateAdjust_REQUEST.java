package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command adjusts the rate of advance of Clock and Time to provide a better
 *  approximation to real time.
 */
public class TPM2_ClockRateAdjust_REQUEST extends ReqStructure
{
    /** TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *  Auth Handle: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE auth;
    
    /** Adjustment to current Clock update rate  */
    public TPM_CLOCK_ADJUST rateAdjust;
    
    public TPM2_ClockRateAdjust_REQUEST() { auth = new TPM_HANDLE(); }
    
    /** @param _auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *         Auth Handle: 1
     *         Auth Role: USER
     *  @param _rateAdjust Adjustment to current Clock update rate
     */
    public TPM2_ClockRateAdjust_REQUEST(TPM_HANDLE _auth, TPM_CLOCK_ADJUST _rateAdjust)
    {
        auth = _auth;
        rateAdjust = _rateAdjust;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { rateAdjust.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { rateAdjust = TPM_CLOCK_ADJUST.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_ClockRateAdjust_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ClockRateAdjust_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ClockRateAdjust_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_ClockRateAdjust_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ClockRateAdjust_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ClockRateAdjust_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "TPM_CLOCK_ADJUST", "rateAdjust", rateAdjust);
    }

    @Override
    public int numHandles() { return 1; }
    
    public int numAuthHandles() { return 1; }
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {auth}; }
}

//<<<
