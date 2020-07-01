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
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeInt(pinCount);
        buf.writeInt(pinLimit);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        pinCount = buf.readInt();
        pinLimit = buf.readInt();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_NV_PIN_COUNTER_PARAMETERS fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_NV_PIN_COUNTER_PARAMETERS.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_NV_PIN_COUNTER_PARAMETERS fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_NV_PIN_COUNTER_PARAMETERS fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_NV_PIN_COUNTER_PARAMETERS.class);
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
