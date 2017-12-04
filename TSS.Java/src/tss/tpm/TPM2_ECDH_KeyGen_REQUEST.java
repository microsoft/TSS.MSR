package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe [de]G). It uses the private ephemeral key and a loaded public key (QS) to compute the shared secret value (P [hde]QS).
*/
public class TPM2_ECDH_KeyGen_REQUEST extends TpmStructure
{
    /**
     * This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe [de]G). It uses the private ephemeral key and a loaded public key (QS) to compute the shared secret value (P [hde]QS).
     * 
     * @param _keyHandle Handle of a loaded ECC key public area. Auth Index: None
     */
    public TPM2_ECDH_KeyGen_REQUEST(TPM_HANDLE _keyHandle)
    {
        keyHandle = _keyHandle;
    }
    /**
    * This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe [de]G). It uses the private ephemeral key and a loaded public key (QS) to compute the shared secret value (P [hde]QS).
    */
    public TPM2_ECDH_KeyGen_REQUEST() {};
    /**
    * Handle of a loaded ECC key public area. Auth Index: None
    */
    public TPM_HANDLE keyHandle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        keyHandle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        keyHandle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ECDH_KeyGen_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ECDH_KeyGen_REQUEST ret = new TPM2_ECDH_KeyGen_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ECDH_KeyGen_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ECDH_KeyGen_REQUEST ret = new TPM2_ECDH_KeyGen_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECDH_KeyGen_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
    };
    
    
};

//<<<

