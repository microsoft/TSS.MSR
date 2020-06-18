package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows qualification of duplication to allow duplication to a selected
 *  new parent.
 */
public class TPM2_PolicyDuplicationSelect_REQUEST extends TpmStructure
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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(objectName);
        buf.writeSizedByteBuf(newParentName);
        buf.writeByte(includeObject);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _objectNameSize = buf.readShort() & 0xFFFF;
        objectName = new byte[_objectNameSize];
        buf.readArrayOfInts(objectName, 1, _objectNameSize);
        int _newParentNameSize = buf.readShort() & 0xFFFF;
        newParentName = new byte[_newParentNameSize];
        buf.readArrayOfInts(newParentName, 1, _newParentNameSize);
        includeObject = buf.readByte();
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_PolicyDuplicationSelect_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_PolicyDuplicationSelect_REQUEST ret = new TPM2_PolicyDuplicationSelect_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyDuplicationSelect_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_PolicyDuplicationSelect_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyDuplicationSelect_REQUEST ret = new TPM2_PolicyDuplicationSelect_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
}

//<<<
