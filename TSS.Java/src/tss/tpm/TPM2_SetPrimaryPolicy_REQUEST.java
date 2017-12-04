package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows setting of the authorization policy for the lockout (lockoutPolicy), the platform hierarchy (platformPolicy), the storage hierarchy (ownerPolicy), and the endorsement hierarchy (endorsementPolicy).
*/
public class TPM2_SetPrimaryPolicy_REQUEST extends TpmStructure
{
    /**
     * This command allows setting of the authorization policy for the lockout (lockoutPolicy), the platform hierarchy (platformPolicy), the storage hierarchy (ownerPolicy), and the endorsement hierarchy (endorsementPolicy).
     * 
     * @param _authHandle TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param _authPolicy an authorization policy digest; may be the Empty Buffer If hashAlg is TPM_ALG_NULL, then this shall be an Empty Buffer. 
     * @param _hashAlg the hash algorithm to use for the policy If the authPolicy is an Empty Buffer, then this field shall be TPM_ALG_NULL.
     */
    public TPM2_SetPrimaryPolicy_REQUEST(TPM_HANDLE _authHandle,byte[] _authPolicy,TPM_ALG_ID _hashAlg)
    {
        authHandle = _authHandle;
        authPolicy = _authPolicy;
        hashAlg = _hashAlg;
    }
    /**
    * This command allows setting of the authorization policy for the lockout (lockoutPolicy), the platform hierarchy (platformPolicy), the storage hierarchy (ownerPolicy), and the endorsement hierarchy (endorsementPolicy).
    */
    public TPM2_SetPrimaryPolicy_REQUEST() {};
    /**
    * TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short authPolicySize;
    /**
    * an authorization policy digest; may be the Empty Buffer If hashAlg is TPM_ALG_NULL, then this shall be an Empty Buffer.
    */
    public byte[] authPolicy;
    /**
    * the hash algorithm to use for the policy If the authPolicy is an Empty Buffer, then this field shall be TPM_ALG_NULL.
    */
    public TPM_ALG_ID hashAlg;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
        buf.writeInt((authPolicy!=null)?authPolicy.length:0, 2);
        if(authPolicy!=null)
            buf.write(authPolicy);
        hashAlg.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
        int _authPolicySize = buf.readInt(2);
        authPolicy = new byte[_authPolicySize];
        buf.readArrayOfInts(authPolicy, 1, _authPolicySize);
        hashAlg = TPM_ALG_ID.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_SetPrimaryPolicy_REQUEST fromTpm (byte[] x) 
    {
        TPM2_SetPrimaryPolicy_REQUEST ret = new TPM2_SetPrimaryPolicy_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_SetPrimaryPolicy_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_SetPrimaryPolicy_REQUEST ret = new TPM2_SetPrimaryPolicy_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SetPrimaryPolicy_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "byte", "authPolicy", authPolicy);
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
    };
    
    
};

//<<<

