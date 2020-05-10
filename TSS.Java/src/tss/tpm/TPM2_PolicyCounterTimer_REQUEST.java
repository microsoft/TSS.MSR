package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command is used to cause conditional gating of a policy based on the contents of
 *  the TPMS_TIME_INFO structure.
 */
public class TPM2_PolicyCounterTimer_REQUEST extends TpmStructure
{
    /**
     *  handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /** the second operand */
    public byte[] operandB;
    
    /** the octet offset in the TPMS_TIME_INFO structure for the start of operand A */
    public short offset;
    
    /** the comparison to make */
    public TPM_EO operation;
    
    public TPM2_PolicyCounterTimer_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /**
     *  @param _policySession handle for the policy session being extended
     *         Auth Index: None
     *  @param _operandB the second operand
     *  @param _offset the octet offset in the TPMS_TIME_INFO structure for the start of operand A
     *  @param _operation the comparison to make
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
        policySession.toTpm(buf);
        buf.writeSizedByteBuf(operandB);
        buf.writeShort(offset);
        operation.toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
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
        return buf.getBuf();
    }

    public static TPM2_PolicyCounterTimer_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyCounterTimer_REQUEST ret = new TPM2_PolicyCounterTimer_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

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

