package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows qualification of duplication to allow duplication to a selected
 *  new parent.
 */
public class TPM2_PolicyDuplicationSelect_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /** The Name of the object to be duplicated  */
    public byte[] objectName;
    
    /** The Name of the new parent  */
    public byte[] newParentName;
    
    /** If YES, the objectName will be included in the value in policySessionpolicyDigest  */
    public byte includeObject;
    
    public TPM2_PolicyDuplicationSelect_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _objectName The Name of the object to be duplicated
     *  @param _newParentName The Name of the new parent
     *  @param _includeObject If YES, the objectName will be included in the value in
     *         policySessionpolicyDigest
     */
    public TPM2_PolicyDuplicationSelect_REQUEST(TPM_HANDLE _policySession, byte[] _objectName, byte[] _newParentName, byte _includeObject)
    {
        policySession = _policySession;
        objectName = _objectName;
        newParentName = _newParentName;
        includeObject = _includeObject;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(objectName);
        buf.writeSizedByteBuf(newParentName);
        buf.writeByte(includeObject);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        objectName = buf.readSizedByteBuf();
        newParentName = buf.readSizedByteBuf();
        includeObject = buf.readByte();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyDuplicationSelect_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyDuplicationSelect_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyDuplicationSelect_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyDuplicationSelect_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyDuplicationSelect_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyDuplicationSelect_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "objectName", objectName);
        _p.add(d, "byte", "newParentName", newParentName);
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
