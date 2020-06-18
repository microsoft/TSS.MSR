package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to cause conditional gating of a policy based on the contents of
 *  the TPMS_TIME_INFO structure.
 */
public class TPM2_PolicyCounterTimer_REQUEST extends TpmStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /** The second operand  */
    public byte[] operandB;
    
    /** The octet offset in the TPMS_TIME_INFO structure for the start of operand A  */
    public short offset;
    
    /** The comparison to make  */
    public TPM_EO operation;
    
    public TPM2_PolicyCounterTimer_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _operandB The second operand
     *  @param _offset The octet offset in the TPMS_TIME_INFO structure for the start of
     *  operand A
     *  @param _operation The comparison to make
     */
    public TPM2_PolicyCounterTimer_REQUEST(TPM_HANDLE _policySession, byte[] _operandB, int _offset, TPM_EO _operation)
    {
        policySession = _policySession;
        operandB = _operandB;
        offset = (short)_offset;
        operation = _operation;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(operandB);
        buf.writeShort(offset);
        operation.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _operandBSize = buf.readShort() & 0xFFFF;
        operandB = new byte[_operandBSize];
        buf.readArrayOfInts(operandB, 1, _operandBSize);
        offset = buf.readShort();
        operation = TPM_EO.fromTpm(buf);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_PolicyCounterTimer_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_PolicyCounterTimer_REQUEST ret = new TPM2_PolicyCounterTimer_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyCounterTimer_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_PolicyCounterTimer_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyCounterTimer_REQUEST ret = new TPM2_PolicyCounterTimer_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyCounterTimer_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "operandB", operandB);
        _p.add(d, "short", "offset", offset);
        _p.add(d, "TPM_EO", "operation", operation);
    }
}

//<<<
