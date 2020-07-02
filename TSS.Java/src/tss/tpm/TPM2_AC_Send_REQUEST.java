package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The purpose of this command is to send (copy) a loaded object from the TPM to an
 *  Attached Component.
 */
public class TPM2_AC_Send_REQUEST extends ReqStructure
{
    /** Handle of the object being sent to ac
     *  Auth Index: 1
     *  Auth Role: DUP
     */
    public TPM_HANDLE sendObject;
    
    /** The handle indicating the source of the authorization value
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** Handle indicating the Attached Component to which the object will be sent
     *  Auth Index: None
     */
    public TPM_HANDLE ac;
    
    /** Optional non sensitive information related to the object  */
    public byte[] acDataIn;
    
    public TPM2_AC_Send_REQUEST()
    {
        sendObject = new TPM_HANDLE();
        authHandle = new TPM_HANDLE();
        ac = new TPM_HANDLE();
    }
    
    /** @param _sendObject Handle of the object being sent to ac
     *         Auth Index: 1
     *         Auth Role: DUP
     *  @param _authHandle The handle indicating the source of the authorization value
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _ac Handle indicating the Attached Component to which the object will be sent
     *         Auth Index: None
     *  @param _acDataIn Optional non sensitive information related to the object
     */
    public TPM2_AC_Send_REQUEST(TPM_HANDLE _sendObject, TPM_HANDLE _authHandle, TPM_HANDLE _ac, byte[] _acDataIn)
    {
        sendObject = _sendObject;
        authHandle = _authHandle;
        ac = _ac;
        acDataIn = _acDataIn;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(acDataIn); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { acDataIn = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_AC_Send_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_AC_Send_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_AC_Send_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_AC_Send_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_AC_Send_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_AC_Send_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "sendObject", sendObject);
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "ac", ac);
        _p.add(d, "byte", "acDataIn", acDataIn);
    }

    @Override
    public int numHandles() { return 3; }

    @Override
    public int numAuthHandles() { return 2; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {sendObject, authHandle, ac}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
