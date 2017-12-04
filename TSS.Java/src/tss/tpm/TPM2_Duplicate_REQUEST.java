package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command duplicates a loaded object so that it may be used in a different hierarchy. The new parent key for the duplicate may be on the same or different TPM or TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
*/
public class TPM2_Duplicate_REQUEST extends TpmStructure
{
    /**
     * This command duplicates a loaded object so that it may be used in a different hierarchy. The new parent key for the duplicate may be on the same or different TPM or TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
     * 
     * @param _objectHandle loaded object to duplicate Auth Index: 1 Auth Role: DUP 
     * @param _newParentHandle shall reference the public area of an asymmetric key Auth Index: None 
     * @param _encryptionKeyIn optional symmetric encryption key The size for this key is set to zero when the TPM is to generate the key. This parameter may be encrypted. 
     * @param _symmetricAlg definition for the symmetric algorithm to be used for the inner wrapper may be TPM_ALG_NULL if no inner wrapper is applied
     */
    public TPM2_Duplicate_REQUEST(TPM_HANDLE _objectHandle,TPM_HANDLE _newParentHandle,byte[] _encryptionKeyIn,TPMT_SYM_DEF_OBJECT _symmetricAlg)
    {
        objectHandle = _objectHandle;
        newParentHandle = _newParentHandle;
        encryptionKeyIn = _encryptionKeyIn;
        symmetricAlg = _symmetricAlg;
    }
    /**
    * This command duplicates a loaded object so that it may be used in a different hierarchy. The new parent key for the duplicate may be on the same or different TPM or TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
    */
    public TPM2_Duplicate_REQUEST() {};
    /**
    * loaded object to duplicate Auth Index: 1 Auth Role: DUP
    */
    public TPM_HANDLE objectHandle;
    /**
    * shall reference the public area of an asymmetric key Auth Index: None
    */
    public TPM_HANDLE newParentHandle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short encryptionKeyInSize;
    /**
    * optional symmetric encryption key The size for this key is set to zero when the TPM is to generate the key. This parameter may be encrypted.
    */
    public byte[] encryptionKeyIn;
    /**
    * definition for the symmetric algorithm to be used for the inner wrapper may be TPM_ALG_NULL if no inner wrapper is applied
    */
    public TPMT_SYM_DEF_OBJECT symmetricAlg;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        objectHandle.toTpm(buf);
        newParentHandle.toTpm(buf);
        buf.writeInt((encryptionKeyIn!=null)?encryptionKeyIn.length:0, 2);
        if(encryptionKeyIn!=null)
            buf.write(encryptionKeyIn);
        symmetricAlg.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        objectHandle = TPM_HANDLE.fromTpm(buf);
        newParentHandle = TPM_HANDLE.fromTpm(buf);
        int _encryptionKeyInSize = buf.readInt(2);
        encryptionKeyIn = new byte[_encryptionKeyInSize];
        buf.readArrayOfInts(encryptionKeyIn, 1, _encryptionKeyInSize);
        symmetricAlg = TPMT_SYM_DEF_OBJECT.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_Duplicate_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Duplicate_REQUEST ret = new TPM2_Duplicate_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_Duplicate_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Duplicate_REQUEST ret = new TPM2_Duplicate_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Duplicate_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "TPM_HANDLE", "newParentHandle", newParentHandle);
        _p.add(d, "byte", "encryptionKeyIn", encryptionKeyIn);
        _p.add(d, "TPMT_SYM_DEF_OBJECT", "symmetricAlg", symmetricAlg);
    };
    
    
};

//<<<

