package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows qualification of the sending (copying) of an Object to an Attached
 *  Component (AC). Qualification includes selection of the receiving AC and the method of
 *  authentication for the AC, and, in certain circumstances, the Object to be sent may be
 *  specified.
 */
public class TPM2_Policy_AC_SendSelect_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /** The Name of the Object to be sent  */
    public byte[] objectName;
    
    /** The Name associated with authHandle used in the TPM2_AC_Send() command  */
    public byte[] authHandleName;
    
    /** The Name of the Attached Component to which the Object will be sent  */
    public byte[] acName;
    
    /** If SET, objectName will be included in the value in policySessionpolicyDigest  */
    public byte includeObject;
    
    public TPM2_Policy_AC_SendSelect_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _objectName The Name of the Object to be sent
     *  @param _authHandleName The Name associated with authHandle used in the TPM2_AC_Send() command
     *  @param _acName The Name of the Attached Component to which the Object will be sent
     *  @param _includeObject If SET, objectName will be included in the value in
     *  policySessionpolicyDigest
     */
    public TPM2_Policy_AC_SendSelect_REQUEST(TPM_HANDLE _policySession, byte[] _objectName, byte[] _authHandleName, byte[] _acName, byte _includeObject)
    {
        policySession = _policySession;
        objectName = _objectName;
        authHandleName = _authHandleName;
        acName = _acName;
        includeObject = _includeObject;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(objectName);
        buf.writeSizedByteBuf(authHandleName);
        buf.writeSizedByteBuf(acName);
        buf.writeByte(includeObject);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        objectName = buf.readSizedByteBuf();
        authHandleName = buf.readSizedByteBuf();
        acName = buf.readSizedByteBuf();
        includeObject = buf.readByte();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_Policy_AC_SendSelect_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Policy_AC_SendSelect_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Policy_AC_SendSelect_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_Policy_AC_SendSelect_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Policy_AC_SendSelect_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Policy_AC_SendSelect_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "objectName", objectName);
        _p.add(d, "byte", "authHandleName", authHandleName);
        _p.add(d, "byte", "acName", acName);
        _p.add(d, "byte", "includeObject", includeObject);
    }

    @Override
    public int numHandles() { return 1; }
    
    public int numAuthHandles() { return 0; }
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {policySession}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
