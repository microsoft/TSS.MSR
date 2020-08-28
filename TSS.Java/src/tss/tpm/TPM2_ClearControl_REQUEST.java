package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** TPM2_ClearControl() disables and enables the execution of TPM2_Clear().  */
public class TPM2_ClearControl_REQUEST extends ReqStructure
{
    /** TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP}
     *  Auth Handle: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE auth;

    /** YES if the disableOwnerClear flag is to be SET, NO if the flag is to be CLEAR.  */
    public byte disable;

    public TPM2_ClearControl_REQUEST() { auth = new TPM_HANDLE(); }

    /** @param _auth TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP}
     *         Auth Handle: 1
     *         Auth Role: USER
     *  @param _disable YES if the disableOwnerClear flag is to be SET, NO if the flag is to
     *  be CLEAR.
     */
    public TPM2_ClearControl_REQUEST(TPM_HANDLE _auth, byte _disable)
    {
        auth = _auth;
        disable = _disable;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeByte(disable); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { disable = buf.readByte(); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_ClearControl_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ClearControl_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ClearControl_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_ClearControl_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ClearControl_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ClearControl_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "byte", "disable", disable);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {auth}; }
}

//<<<
