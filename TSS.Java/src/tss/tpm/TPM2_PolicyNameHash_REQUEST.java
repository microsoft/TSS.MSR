package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows a policy to be bound to a specific set of TPM entities without being bound to the parameters of the command. This is most useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event() when the referenced PCR requires a policy.
*/
public class TPM2_PolicyNameHash_REQUEST extends TpmStructure
{
    /**
     * This command allows a policy to be bound to a specific set of TPM entities without being bound to the parameters of the command. This is most useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event() when the referenced PCR requires a policy.
     * 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _nameHash the digest to be added to the policy
     */
    public TPM2_PolicyNameHash_REQUEST(TPM_HANDLE _policySession,byte[] _nameHash)
    {
        policySession = _policySession;
        nameHash = _nameHash;
    }
    /**
    * This command allows a policy to be bound to a specific set of TPM entities without being bound to the parameters of the command. This is most useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event() when the referenced PCR requires a policy.
    */
    public TPM2_PolicyNameHash_REQUEST() {};
    /**
    * handle for the policy session being extended Auth Index: None
    */
    public TPM_HANDLE policySession;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short nameHashSize;
    /**
    * the digest to be added to the policy
    */
    public byte[] nameHash;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        buf.writeInt((nameHash!=null)?nameHash.length:0, 2);
        if(nameHash!=null)
            buf.write(nameHash);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        int _nameHashSize = buf.readInt(2);
        nameHash = new byte[_nameHashSize];
        buf.readArrayOfInts(nameHash, 1, _nameHashSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyNameHash_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyNameHash_REQUEST ret = new TPM2_PolicyNameHash_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyNameHash_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyNameHash_REQUEST ret = new TPM2_PolicyNameHash_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyNameHash_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "nameHash", nameHash);
    };
    
    
};

//<<<

