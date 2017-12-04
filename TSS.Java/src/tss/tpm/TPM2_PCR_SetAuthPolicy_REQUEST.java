package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to associate a policy with a PCR or group of PCR. The policy determines the conditions under which a PCR may be extended or reset.
*/
public class TPM2_PCR_SetAuthPolicy_REQUEST extends TpmStructure
{
    /**
     * This command is used to associate a policy with a PCR or group of PCR. The policy determines the conditions under which a PCR may be extended or reset.
     * 
     * @param _authHandle TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param _authPolicy the desired authPolicy 
     * @param _hashAlg the hash algorithm of the policy 
     * @param _pcrNum the PCR for which the policy is to be set
     */
    public TPM2_PCR_SetAuthPolicy_REQUEST(TPM_HANDLE _authHandle,byte[] _authPolicy,TPM_ALG_ID _hashAlg,TPM_HANDLE _pcrNum)
    {
        authHandle = _authHandle;
        authPolicy = _authPolicy;
        hashAlg = _hashAlg;
        pcrNum = _pcrNum;
    }
    /**
    * This command is used to associate a policy with a PCR or group of PCR. The policy determines the conditions under which a PCR may be extended or reset.
    */
    public TPM2_PCR_SetAuthPolicy_REQUEST() {};
    /**
    * TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short authPolicySize;
    /**
    * the desired authPolicy
    */
    public byte[] authPolicy;
    /**
    * the hash algorithm of the policy
    */
    public TPM_ALG_ID hashAlg;
    /**
    * the PCR for which the policy is to be set
    */
    public TPM_HANDLE pcrNum;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
        buf.writeInt((authPolicy!=null)?authPolicy.length:0, 2);
        if(authPolicy!=null)
            buf.write(authPolicy);
        hashAlg.toTpm(buf);
        pcrNum.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
        int _authPolicySize = buf.readInt(2);
        authPolicy = new byte[_authPolicySize];
        buf.readArrayOfInts(authPolicy, 1, _authPolicySize);
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        pcrNum = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PCR_SetAuthPolicy_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PCR_SetAuthPolicy_REQUEST ret = new TPM2_PCR_SetAuthPolicy_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PCR_SetAuthPolicy_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PCR_SetAuthPolicy_REQUEST ret = new TPM2_PCR_SetAuthPolicy_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_SetAuthPolicy_REQUEST");
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
        _p.add(d, "TPM_HANDLE", "pcrNum", pcrNum);
    };
    
    
};

//<<<

