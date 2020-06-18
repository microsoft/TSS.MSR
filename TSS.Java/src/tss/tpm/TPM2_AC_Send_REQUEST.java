package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The purpose of this command is to send (copy) a loaded object from the TPM to an
 *  Attached Component.
 */
public class TPM2_AC_Send_REQUEST extends TpmStructure
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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(acDataIn);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _acDataInSize = buf.readShort() & 0xFFFF;
        acDataIn = new byte[_acDataInSize];
        buf.readArrayOfInts(acDataIn, 1, _acDataInSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_AC_Send_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_AC_Send_REQUEST ret = new TPM2_AC_Send_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_AC_Send_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_AC_Send_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_AC_Send_REQUEST ret = new TPM2_AC_Send_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
}

//<<<
