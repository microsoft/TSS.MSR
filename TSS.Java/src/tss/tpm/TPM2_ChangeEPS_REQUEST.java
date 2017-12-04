package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This replaces the current endorsement primary seed (EPS) with a value from the RNG and sets the Endorsement hierarchy controls to their default initialization values: ehEnable is SET, endorsementAuth and endorsementPolicy are both set to the Empty Buffer. It will flush any resident objects (transient or persistent) in the Endorsement hierarchy and not allow objects in the hierarchy associated with the previous EPS to be loaded.
*/
public class TPM2_ChangeEPS_REQUEST extends TpmStructure
{
    /**
     * This replaces the current endorsement primary seed (EPS) with a value from the RNG and sets the Endorsement hierarchy controls to their default initialization values: ehEnable is SET, endorsementAuth and endorsementPolicy are both set to the Empty Buffer. It will flush any resident objects (transient or persistent) in the Endorsement hierarchy and not allow objects in the hierarchy associated with the previous EPS to be loaded.
     * 
     * @param _authHandle TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER
     */
    public TPM2_ChangeEPS_REQUEST(TPM_HANDLE _authHandle)
    {
        authHandle = _authHandle;
    }
    /**
    * This replaces the current endorsement primary seed (EPS) with a value from the RNG and sets the Endorsement hierarchy controls to their default initialization values: ehEnable is SET, endorsementAuth and endorsementPolicy are both set to the Empty Buffer. It will flush any resident objects (transient or persistent) in the Endorsement hierarchy and not allow objects in the hierarchy associated with the previous EPS to be loaded.
    */
    public TPM2_ChangeEPS_REQUEST() {};
    /**
    * TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ChangeEPS_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ChangeEPS_REQUEST ret = new TPM2_ChangeEPS_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ChangeEPS_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ChangeEPS_REQUEST ret = new TPM2_ChangeEPS_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ChangeEPS_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
    };
    
    
};

//<<<

