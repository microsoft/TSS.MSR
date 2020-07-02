package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to advance the value of the TPMs Clock. The command will fail if
 *  newTime is less than the current value of Clock or if the new time is greater than
 *  FFFF00000000000016. If both of these checks succeed, Clock is set to newTime. If
 *  either of these checks fails, the TPM shall return TPM_RC_VALUE and make no change to Clock.
 */
public class TPM2_ClockSet_REQUEST extends ReqStructure
{
    /** TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *  Auth Handle: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE auth;
    
    /** New Clock setting in milliseconds  */
    public long newTime;
    
    public TPM2_ClockSet_REQUEST() { auth = new TPM_HANDLE(); }
    
    /** @param _auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *         Auth Handle: 1
     *         Auth Role: USER
     *  @param _newTime New Clock setting in milliseconds
     */
    public TPM2_ClockSet_REQUEST(TPM_HANDLE _auth, long _newTime)
    {
        auth = _auth;
        newTime = _newTime;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeInt64(newTime); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { newTime = buf.readInt64(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_ClockSet_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ClockSet_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ClockSet_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_ClockSet_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ClockSet_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ClockSet_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "long", "newTime", newTime);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {auth}; }
}

//<<<
