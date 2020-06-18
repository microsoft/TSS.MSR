package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command adjusts the rate of advance of Clock and Time to provide a better
 *  approximation to real time.
 */
public class TPM2_ClockRateAdjust_REQUEST extends TpmStructure
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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        rateAdjust.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        rateAdjust = TPM_CLOCK_ADJUST.fromTpm(buf);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_ClockRateAdjust_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_ClockRateAdjust_REQUEST ret = new TPM2_ClockRateAdjust_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ClockRateAdjust_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_ClockRateAdjust_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ClockRateAdjust_REQUEST ret = new TPM2_ClockRateAdjust_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
}

//<<<
