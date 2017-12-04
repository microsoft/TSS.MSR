package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows qualification of duplication to allow duplication to a selected new parent.
*/
public class TPM2_PolicyDuplicationSelect_REQUEST extends TpmStructure
{
    /**
     * This command allows qualification of duplication to allow duplication to a selected new parent.
     * 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _objectName the Name of the object to be duplicated 
     * @param _newParentName the Name of the new parent 
     * @param _includeObject if YES, the objectName will be included in the value in policySessionpolicyDigest
     */
    public TPM2_PolicyDuplicationSelect_REQUEST(TPM_HANDLE _policySession,byte[] _objectName,byte[] _newParentName,byte _includeObject)
    {
        policySession = _policySession;
        objectName = _objectName;
        newParentName = _newParentName;
        includeObject = _includeObject;
    }
    /**
    * This command allows qualification of duplication to allow duplication to a selected new parent.
    */
    public TPM2_PolicyDuplicationSelect_REQUEST() {};
    /**
    * handle for the policy session being extended Auth Index: None
    */
    public TPM_HANDLE policySession;
    /**
    * size of the Name structure
    */
    // private short objectNameSize;
    /**
    * the Name of the object to be duplicated
    */
    public byte[] objectName;
    /**
    * size of the Name structure
    */
    // private short newParentNameSize;
    /**
    * the Name of the new parent
    */
    public byte[] newParentName;
    /**
    * if YES, the objectName will be included in the value in policySessionpolicyDigest
    */
    public byte includeObject;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        buf.writeInt((objectName!=null)?objectName.length:0, 2);
        if(objectName!=null)
            buf.write(objectName);
        buf.writeInt((newParentName!=null)?newParentName.length:0, 2);
        if(newParentName!=null)
            buf.write(newParentName);
        buf.write(includeObject);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        int _objectNameSize = buf.readInt(2);
        objectName = new byte[_objectNameSize];
        buf.readArrayOfInts(objectName, 1, _objectNameSize);
        int _newParentNameSize = buf.readInt(2);
        newParentName = new byte[_newParentNameSize];
        buf.readArrayOfInts(newParentName, 1, _newParentNameSize);
        includeObject = (byte) buf.readInt(1);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyDuplicationSelect_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyDuplicationSelect_REQUEST ret = new TPM2_PolicyDuplicationSelect_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
        _p.add(d, "BYTE", "includeObject", includeObject);
    };
    
    
};

//<<<

