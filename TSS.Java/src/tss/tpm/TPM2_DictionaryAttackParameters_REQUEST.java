package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command changes the lockout parameters.
*/
public class TPM2_DictionaryAttackParameters_REQUEST extends TpmStructure
{
    /**
     * This command changes the lockout parameters.
     * 
     * @param _lockHandle TPM_RH_LOCKOUT Auth Index: 1 Auth Role: USER 
     * @param _newMaxTries count of authorization failures before the lockout is imposed 
     * @param _newRecoveryTime time in seconds before the authorization failure count is automatically decremented A value of zero indicates that DA protection is disabled. 
     * @param _lockoutRecovery time in seconds after a lockoutAuth failure before use of lockoutAuth is allowed A value of zero indicates that a reboot is required.
     */
    public TPM2_DictionaryAttackParameters_REQUEST(TPM_HANDLE _lockHandle,int _newMaxTries,int _newRecoveryTime,int _lockoutRecovery)
    {
        lockHandle = _lockHandle;
        newMaxTries = _newMaxTries;
        newRecoveryTime = _newRecoveryTime;
        lockoutRecovery = _lockoutRecovery;
    }
    /**
    * This command changes the lockout parameters.
    */
    public TPM2_DictionaryAttackParameters_REQUEST() {};
    /**
    * TPM_RH_LOCKOUT Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE lockHandle;
    /**
    * count of authorization failures before the lockout is imposed
    */
    public int newMaxTries;
    /**
    * time in seconds before the authorization failure count is automatically decremented A value of zero indicates that DA protection is disabled.
    */
    public int newRecoveryTime;
    /**
    * time in seconds after a lockoutAuth failure before use of lockoutAuth is allowed A value of zero indicates that a reboot is required.
    */
    public int lockoutRecovery;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        lockHandle.toTpm(buf);
        buf.write(newMaxTries);
        buf.write(newRecoveryTime);
        buf.write(lockoutRecovery);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        lockHandle = TPM_HANDLE.fromTpm(buf);
        newMaxTries =  buf.readInt(4);
        newRecoveryTime =  buf.readInt(4);
        lockoutRecovery =  buf.readInt(4);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_DictionaryAttackParameters_REQUEST fromTpm (byte[] x) 
    {
        TPM2_DictionaryAttackParameters_REQUEST ret = new TPM2_DictionaryAttackParameters_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_DictionaryAttackParameters_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_DictionaryAttackParameters_REQUEST ret = new TPM2_DictionaryAttackParameters_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_DictionaryAttackParameters_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "lockHandle", lockHandle);
        _p.add(d, "uint", "newMaxTries", newMaxTries);
        _p.add(d, "uint", "newRecoveryTime", newRecoveryTime);
        _p.add(d, "uint", "lockoutRecovery", lockoutRecovery);
    };
    
    
};

//<<<

