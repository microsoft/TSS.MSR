package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command cancels the effect of a TPM lockout due to a number of successive authorization failures. If this command is properly authorized, the lockout counter is set to zero.
*/
public class TPM2_DictionaryAttackLockReset_REQUEST extends TpmStructure
{
    /**
     * This command cancels the effect of a TPM lockout due to a number of successive authorization failures. If this command is properly authorized, the lockout counter is set to zero.
     * 
     * @param _lockHandle TPM_RH_LOCKOUT Auth Index: 1 Auth Role: USER
     */
    public TPM2_DictionaryAttackLockReset_REQUEST(TPM_HANDLE _lockHandle)
    {
        lockHandle = _lockHandle;
    }
    /**
    * This command cancels the effect of a TPM lockout due to a number of successive authorization failures. If this command is properly authorized, the lockout counter is set to zero.
    */
    public TPM2_DictionaryAttackLockReset_REQUEST() {};
    /**
    * TPM_RH_LOCKOUT Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE lockHandle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        lockHandle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        lockHandle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_DictionaryAttackLockReset_REQUEST fromTpm (byte[] x) 
    {
        TPM2_DictionaryAttackLockReset_REQUEST ret = new TPM2_DictionaryAttackLockReset_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_DictionaryAttackLockReset_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_DictionaryAttackLockReset_REQUEST ret = new TPM2_DictionaryAttackLockReset_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_DictionaryAttackLockReset_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "lockHandle", lockHandle);
    };
    
    
};

//<<<

