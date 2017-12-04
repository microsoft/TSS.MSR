package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* The purpose of this command is to send (copy) a loaded object from the TPM to an Attached Component.
*/
public class TPM2_AC_Send_REQUEST extends TpmStructure
{
    /**
     * The purpose of this command is to send (copy) a loaded object from the TPM to an Attached Component.
     * 
     * @param _sendObject handle of the object being sent to ac Auth Index: 1 Auth Role: DUP 
     * @param _authHandle the handle indicating the source of the authorization value Auth Index: 2 Auth Role: USER 
     * @param _ac handle indicating the Attached Component to which the object will be sent Auth Index: None 
     * @param _acDataIn Optional non sensitive information related to the object
     */
    public TPM2_AC_Send_REQUEST(TPM_HANDLE _sendObject,TPM_HANDLE _authHandle,TPM_HANDLE _ac,byte[] _acDataIn)
    {
        sendObject = _sendObject;
        authHandle = _authHandle;
        ac = _ac;
        acDataIn = _acDataIn;
    }
    /**
    * The purpose of this command is to send (copy) a loaded object from the TPM to an Attached Component.
    */
    public TPM2_AC_Send_REQUEST() {};
    /**
    * handle of the object being sent to ac Auth Index: 1 Auth Role: DUP
    */
    public TPM_HANDLE sendObject;
    /**
    * the handle indicating the source of the authorization value Auth Index: 2 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * handle indicating the Attached Component to which the object will be sent Auth Index: None
    */
    public TPM_HANDLE ac;
    /**
    * size of the buffer
    */
    // private short acDataInSize;
    /**
    * Optional non sensitive information related to the object
    */
    public byte[] acDataIn;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        sendObject.toTpm(buf);
        authHandle.toTpm(buf);
        ac.toTpm(buf);
        buf.writeInt((acDataIn!=null)?acDataIn.length:0, 2);
        if(acDataIn!=null)
            buf.write(acDataIn);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        sendObject = TPM_HANDLE.fromTpm(buf);
        authHandle = TPM_HANDLE.fromTpm(buf);
        ac = TPM_HANDLE.fromTpm(buf);
        int _acDataInSize = buf.readInt(2);
        acDataIn = new byte[_acDataInSize];
        buf.readArrayOfInts(acDataIn, 1, _acDataInSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_AC_Send_REQUEST fromTpm (byte[] x) 
    {
        TPM2_AC_Send_REQUEST ret = new TPM2_AC_Send_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
    };
    
    
};

//<<<

