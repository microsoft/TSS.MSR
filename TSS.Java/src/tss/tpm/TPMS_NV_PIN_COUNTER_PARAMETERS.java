package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the data that can be written to and read from a TPM_NT_PIN_PASS or
 *  TPM_NT_PIN_FAIL non-volatile index. pinCount is the most significant octets. pinLimit
 *  is the least significant octets.
 */
public class TPMS_NV_PIN_COUNTER_PARAMETERS extends TpmStructure
{
    /** This counter shows the current number of successful authValue authorization attempts
     *  to access a TPM_NT_PIN_PASS index or the current number of unsuccessful authValue
     *  authorization attempts to access a TPM_NT_PIN_FAIL index.
     */
    public int pinCount;
    
    /** This threshold is the value of pinCount at which the authValue authorization of the
     *  host TPM_NT_PIN_PASS or TPM_NT_PIN_FAIL index is locked out.
     */
    public int pinLimit;
    
    public TPMS_NV_PIN_COUNTER_PARAMETERS() {}
    
    /** @param _pinCount This counter shows the current number of successful authValue
     *         authorization attempts to access a TPM_NT_PIN_PASS index or the current number of
     *         unsuccessful authValue authorization attempts to access a TPM_NT_PIN_FAIL index.
     *  @param _pinLimit This threshold is the value of pinCount at which the authValue
     *         authorization of the host TPM_NT_PIN_PASS or TPM_NT_PIN_FAIL index is locked out.
     */
    public TPMS_NV_PIN_COUNTER_PARAMETERS(int _pinCount, int _pinLimit)
    {
        pinCount = _pinCount;
        pinLimit = _pinLimit;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(pinCount);
        buf.writeInt(pinLimit);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        pinCount = buf.readInt();
        pinLimit = buf.readInt();
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMS_NV_PIN_COUNTER_PARAMETERS fromBytes (byte[] byteBuf) 
    {
        TPMS_NV_PIN_COUNTER_PARAMETERS ret = new TPMS_NV_PIN_COUNTER_PARAMETERS();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_NV_PIN_COUNTER_PARAMETERS fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPMS_NV_PIN_COUNTER_PARAMETERS fromTpm (InByteBuf buf) 
    {
        TPMS_NV_PIN_COUNTER_PARAMETERS ret = new TPMS_NV_PIN_COUNTER_PARAMETERS();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NV_PIN_COUNTER_PARAMETERS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "int", "pinCount", pinCount);
        _p.add(d, "int", "pinLimit", pinLimit);
    }
}

//<<<
