package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows a policy to be bound to a specific creation template. This is most useful for an object creation command such as TPM2_Create(), TPM2_CreatePrimary(), or TPM2_CreateLoaded().
*/
public class TPM2_PolicyTemplate_REQUEST extends TpmStructure
{
    /**
     * This command allows a policy to be bound to a specific creation template. This is most useful for an object creation command such as TPM2_Create(), TPM2_CreatePrimary(), or TPM2_CreateLoaded().
     * 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _templateHash the digest to be added to the policy
     */
    public TPM2_PolicyTemplate_REQUEST(TPM_HANDLE _policySession,byte[] _templateHash)
    {
        policySession = _policySession;
        templateHash = _templateHash;
    }
    /**
    * This command allows a policy to be bound to a specific creation template. This is most useful for an object creation command such as TPM2_Create(), TPM2_CreatePrimary(), or TPM2_CreateLoaded().
    */
    public TPM2_PolicyTemplate_REQUEST() {};
    /**
    * handle for the policy session being extended Auth Index: None
    */
    public TPM_HANDLE policySession;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short templateHashSize;
    /**
    * the digest to be added to the policy
    */
    public byte[] templateHash;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        buf.writeInt((templateHash!=null)?templateHash.length:0, 2);
        if(templateHash!=null)
            buf.write(templateHash);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        int _templateHashSize = buf.readInt(2);
        templateHash = new byte[_templateHashSize];
        buf.readArrayOfInts(templateHash, 1, _templateHashSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyTemplate_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyTemplate_REQUEST ret = new TPM2_PolicyTemplate_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyTemplate_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyTemplate_REQUEST ret = new TPM2_PolicyTemplate_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyTemplate_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "templateHash", templateHash);
    };
    
    
};

//<<<

