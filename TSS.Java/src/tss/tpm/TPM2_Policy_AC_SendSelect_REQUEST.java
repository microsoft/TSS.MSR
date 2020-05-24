package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command allows qualification of the sending (copying) of an Object to an Attached
 *  Component (AC). Qualification includes selection of the receiving AC and the method of
 *  authentication for the AC, and, in certain circumstances, the Object to
 *  be sent may be specified.
 */
public class TPM2_Policy_AC_SendSelect_REQUEST extends TpmStructure
{
    /**
     *  handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /** the Name of the Object to be sent */
    public byte[] objectName;
    
    /** the Name associated with authHandle used in the TPM2_AC_Send() command */
    public byte[] authHandleName;
    
    /** the Name of the Attached Component to which the Object will be sent */
    public byte[] acName;
    
    /** if SET, objectName will be included in the value in policySessionpolicyDigest */
    public byte includeObject;
    
    public TPM2_Policy_AC_SendSelect_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /**
     *  @param _policySession handle for the policy session being extended
     *         Auth Index: None
     *  @param _objectName the Name of the Object to be sent
     *  @param _authHandleName the Name associated with authHandle used in the TPM2_AC_Send() command
     *  @param _acName the Name of the Attached Component to which the Object will be sent
     *  @param _includeObject if SET, objectName will be included in the value in policySessionpolicyDigest
     */
    public TPM2_Policy_AC_SendSelect_REQUEST(TPM_HANDLE _policySession, byte[] _objectName, byte[] _authHandleName, byte[] _acName, byte _includeObject)
    {
        policySession = _policySession;
        objectName = _objectName;
        authHandleName = _authHandleName;
        acName = _acName;
        includeObject = _includeObject;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(objectName);
        buf.writeSizedByteBuf(authHandleName);
        buf.writeSizedByteBuf(acName);
        buf.writeByte(includeObject);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _objectNameSize = buf.readShort() & 0xFFFF;
        objectName = new byte[_objectNameSize];
        buf.readArrayOfInts(objectName, 1, _objectNameSize);
        int _authHandleNameSize = buf.readShort() & 0xFFFF;
        authHandleName = new byte[_authHandleNameSize];
        buf.readArrayOfInts(authHandleName, 1, _authHandleNameSize);
        int _acNameSize = buf.readShort() & 0xFFFF;
        acName = new byte[_acNameSize];
        buf.readArrayOfInts(acName, 1, _acNameSize);
        includeObject = buf.readByte();
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPM2_Policy_AC_SendSelect_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Policy_AC_SendSelect_REQUEST ret = new TPM2_Policy_AC_SendSelect_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_Policy_AC_SendSelect_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Policy_AC_SendSelect_REQUEST ret = new TPM2_Policy_AC_SendSelect_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
}

//<<<
