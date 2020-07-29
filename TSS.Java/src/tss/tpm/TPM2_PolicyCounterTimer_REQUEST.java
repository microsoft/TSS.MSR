package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to cause conditional gating of a policy based on the contents of
 *  the TPMS_TIME_INFO structure.
 */
public class TPM2_PolicyCounterTimer_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /** The second operand  */
    public byte[] operandB;
    
    /** The octet offset in the TPMS_TIME_INFO structure for the start of operand A  */
    public int offset;
    
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
        offset = _offset;
        operation = _operation;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(operandB);
        buf.writeShort(offset);
        operation.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        operandB = buf.readSizedByteBuf();
        offset = buf.readShort();
        operation = TPM_EO.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyCounterTimer_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyCounterTimer_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyCounterTimer_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyCounterTimer_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyCounterTimer_REQUEST.class);
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
        _p.add(d, "int", "offset", offset);
        _p.add(d, "TPM_EO", "operation", operation);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {policySession}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
