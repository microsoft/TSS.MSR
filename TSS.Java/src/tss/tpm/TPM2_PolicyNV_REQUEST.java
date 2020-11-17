package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to cause conditional gating of a policy based on the contents of
 *  an NV Index. It is an immediate assertion. The NV index is validated during the
 *  TPM2_PolicyNV() command, not when the session is used for authorization.
 */
public class TPM2_PolicyNV_REQUEST extends ReqStructure
{
    /** Handle indicating the source of the authorization value
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;

    /** The NV Index of the area to read
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;

    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;

    /** The second operand */
    public byte[] operandB;

    /** The octet offset in the NV Index for the start of operand A */
    public int offset;

    /** The comparison to make */
    public TPM_EO operation;

    public TPM2_PolicyNV_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        nvIndex = new TPM_HANDLE();
        policySession = new TPM_HANDLE();
    }

    /** @param _authHandle Handle indicating the source of the authorization value
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _nvIndex The NV Index of the area to read
     *         Auth Index: None
     *  @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _operandB The second operand
     *  @param _offset The octet offset in the NV Index for the start of operand A
     *  @param _operation The comparison to make
     */
    public TPM2_PolicyNV_REQUEST(TPM_HANDLE _authHandle, TPM_HANDLE _nvIndex, TPM_HANDLE _policySession, byte[] _operandB, int _offset, TPM_EO _operation)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        policySession = _policySession;
        operandB = _operandB;
        offset = _offset;
        operation = _operation;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(operandB);
        buf.writeShort(offset);
        operation.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        operandB = buf.readSizedByteBuf();
        offset = buf.readShort();
        operation = TPM_EO.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicyNV_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyNV_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicyNV_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicyNV_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyNV_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyNV_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte[]", "operandB", operandB);
        _p.add(d, "int", "offset", offset);
        _p.add(d, "TPM_EO", "operation", operation);
    }

    @Override
    public int numHandles() { return 3; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authHandle, nvIndex, policySession}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
