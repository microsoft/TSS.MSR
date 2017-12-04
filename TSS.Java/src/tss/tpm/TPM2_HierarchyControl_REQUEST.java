package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command enables and disables use of a hierarchy and its associated NV storage. The command allows phEnable, phEnableNV, shEnable, and ehEnable to be changed when the proper authorization is provided.
*/
public class TPM2_HierarchyControl_REQUEST extends TpmStructure
{
    /**
     * This command enables and disables use of a hierarchy and its associated NV storage. The command allows phEnable, phEnableNV, shEnable, and ehEnable to be changed when the proper authorization is provided.
     * 
     * @param _authHandle TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param _enable the enable being modified TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM, or TPM_RH_PLATFORM_NV 
     * @param _state YES if the enable should be SET, NO if the enable should be CLEAR
     */
    public TPM2_HierarchyControl_REQUEST(TPM_HANDLE _authHandle,TPM_HANDLE _enable,byte _state)
    {
        authHandle = _authHandle;
        enable = _enable;
        state = _state;
    }
    /**
    * This command enables and disables use of a hierarchy and its associated NV storage. The command allows phEnable, phEnableNV, shEnable, and ehEnable to be changed when the proper authorization is provided.
    */
    public TPM2_HierarchyControl_REQUEST() {};
    /**
    * TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * the enable being modified TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM, or TPM_RH_PLATFORM_NV
    */
    public TPM_HANDLE enable;
    /**
    * YES if the enable should be SET, NO if the enable should be CLEAR
    */
    public byte state;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
        enable.toTpm(buf);
        buf.write(state);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
        enable = TPM_HANDLE.fromTpm(buf);
        state = (byte) buf.readInt(1);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_HierarchyControl_REQUEST fromTpm (byte[] x) 
    {
        TPM2_HierarchyControl_REQUEST ret = new TPM2_HierarchyControl_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_HierarchyControl_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_HierarchyControl_REQUEST ret = new TPM2_HierarchyControl_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HierarchyControl_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "enable", enable);
        _p.add(d, "BYTE", "state", state);
    };
    
    
};

//<<<

