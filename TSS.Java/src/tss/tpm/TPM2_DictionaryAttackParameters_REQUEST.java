package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command changes the lockout parameters. */
public class TPM2_DictionaryAttackParameters_REQUEST extends ReqStructure
{
    /** TPM_RH_LOCKOUT
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE lockHandle;

    /** Count of authorization failures before the lockout is imposed */
    public int newMaxTries;

    /** Time in seconds before the authorization failure count is automatically decremented
     *  A value of zero indicates that DA protection is disabled.
     */
    public int newRecoveryTime;

    /** Time in seconds after a lockoutAuth failure before use of lockoutAuth is allowed
     *  A value of zero indicates that a reboot is required.
     */
    public int lockoutRecovery;

    public TPM2_DictionaryAttackParameters_REQUEST() { lockHandle = new TPM_HANDLE(); }

    /** @param _lockHandle TPM_RH_LOCKOUT
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _newMaxTries Count of authorization failures before the lockout is imposed
     *  @param _newRecoveryTime Time in seconds before the authorization failure count is
     *         automatically decremented
     *         A value of zero indicates that DA protection is disabled.
     *  @param _lockoutRecovery Time in seconds after a lockoutAuth failure before use of
     *         lockoutAuth is allowed
     *         A value of zero indicates that a reboot is required.
     */
    public TPM2_DictionaryAttackParameters_REQUEST(TPM_HANDLE _lockHandle, int _newMaxTries, int _newRecoveryTime, int _lockoutRecovery)
    {
        lockHandle = _lockHandle;
        newMaxTries = _newMaxTries;
        newRecoveryTime = _newRecoveryTime;
        lockoutRecovery = _lockoutRecovery;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeInt(newMaxTries);
        buf.writeInt(newRecoveryTime);
        buf.writeInt(lockoutRecovery);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        newMaxTries = buf.readInt();
        newRecoveryTime = buf.readInt();
        lockoutRecovery = buf.readInt();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_DictionaryAttackParameters_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_DictionaryAttackParameters_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_DictionaryAttackParameters_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_DictionaryAttackParameters_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_DictionaryAttackParameters_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_DictionaryAttackParameters_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "lockHandle", lockHandle);
        _p.add(d, "int", "newMaxTries", newMaxTries);
        _p.add(d, "int", "newRecoveryTime", newRecoveryTime);
        _p.add(d, "int", "lockoutRecovery", lockoutRecovery);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {lockHandle}; }
}

//<<<
