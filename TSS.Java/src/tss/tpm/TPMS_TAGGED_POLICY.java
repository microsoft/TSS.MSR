package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used in TPM2_GetCapability() to return the policy associated with a permanent handle.
*/
public class TPMS_TAGGED_POLICY extends TpmStructure
{
    /**
     * This structure is used in TPM2_GetCapability() to return the policy associated with a permanent handle.
     * 
     * @param _handle a permanent handle 
     * @param _policyHash the policy algorithm and hash
     */
    public TPMS_TAGGED_POLICY(TPM_HANDLE _handle,TPMT_HA _policyHash)
    {
        handle = _handle;
        policyHash = _policyHash;
    }
    /**
    * This structure is used in TPM2_GetCapability() to return the policy associated with a permanent handle.
    */
    public TPMS_TAGGED_POLICY() {};
    /**
    * a permanent handle
    */
    public TPM_HANDLE handle;
    /**
    * the policy algorithm and hash
    */
    public TPMT_HA policyHash;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        policyHash.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        // TODO TpmHash  -- 
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_TAGGED_POLICY fromTpm (byte[] x) 
    {
        TPMS_TAGGED_POLICY ret = new TPMS_TAGGED_POLICY();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_TAGGED_POLICY fromTpm (InByteBuf buf) 
    {
        TPMS_TAGGED_POLICY ret = new TPMS_TAGGED_POLICY();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_TAGGED_POLICY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "TpmHash", "policyHash", policyHash);
    };
    
    
};

//<<<

