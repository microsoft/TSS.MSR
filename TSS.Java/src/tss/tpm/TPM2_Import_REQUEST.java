package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
*/
public class TPM2_Import_REQUEST extends TpmStructure
{
    /**
     * This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
     * 
     * @param _parentHandle the handle of the new parent for the object Auth Index: 1 Auth Role: USER 
     * @param _encryptionKey the optional symmetric encryption key used as the inner wrapper for duplicate If symmetricAlg is TPM_ALG_NULL, then this parameter shall be the Empty Buffer. 
     * @param _objectPublic the public area of the object to be imported This is provided so that the integrity value for duplicate and the object attributes can be checked. NOTE Even if the integrity value of the object is not checked on input, the object Name is required to create the integrity value for the imported object. 
     * @param _duplicate the symmetrically encrypted duplicate object that may contain an inner symmetric wrapper 
     * @param _inSymSeed the seed for the symmetric key and HMAC key inSymSeed is encrypted/encoded using the algorithms of newParent. 
     * @param _symmetricAlg definition for the symmetric algorithm to use for the inner wrapper If this algorithm is TPM_ALG_NULL, no inner wrapper is present and encryptionKey shall be the Empty Buffer.
     */
    public TPM2_Import_REQUEST(TPM_HANDLE _parentHandle,byte[] _encryptionKey,TPMT_PUBLIC _objectPublic,TPM2B_PRIVATE _duplicate,byte[] _inSymSeed,TPMT_SYM_DEF_OBJECT _symmetricAlg)
    {
        parentHandle = _parentHandle;
        encryptionKey = _encryptionKey;
        objectPublic = _objectPublic;
        duplicate = _duplicate;
        inSymSeed = _inSymSeed;
        symmetricAlg = _symmetricAlg;
    }
    /**
    * This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
    */
    public TPM2_Import_REQUEST() {};
    /**
    * the handle of the new parent for the object Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE parentHandle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short encryptionKeySize;
    /**
    * the optional symmetric encryption key used as the inner wrapper for duplicate If symmetricAlg is TPM_ALG_NULL, then this parameter shall be the Empty Buffer.
    */
    public byte[] encryptionKey;
    /**
    * size of publicArea NOTE The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.
    */
    // private short objectPublicSize;
    /**
    * the public area of the object to be imported This is provided so that the integrity value for duplicate and the object attributes can be checked. NOTE Even if the integrity value of the object is not checked on input, the object Name is required to create the integrity value for the imported object.
    */
    public TPMT_PUBLIC objectPublic;
    /**
    * the symmetrically encrypted duplicate object that may contain an inner symmetric wrapper
    */
    public TPM2B_PRIVATE duplicate;
    /**
    * size of the secret value
    */
    // private short inSymSeedSize;
    /**
    * the seed for the symmetric key and HMAC key inSymSeed is encrypted/encoded using the algorithms of newParent.
    */
    public byte[] inSymSeed;
    /**
    * definition for the symmetric algorithm to use for the inner wrapper If this algorithm is TPM_ALG_NULL, no inner wrapper is present and encryptionKey shall be the Empty Buffer.
    */
    public TPMT_SYM_DEF_OBJECT symmetricAlg;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        parentHandle.toTpm(buf);
        buf.writeInt((encryptionKey!=null)?encryptionKey.length:0, 2);
        if(encryptionKey!=null)
            buf.write(encryptionKey);
        buf.writeInt((objectPublic!=null)?objectPublic.toTpm().length:0, 2);
        if(objectPublic!=null)
            objectPublic.toTpm(buf);
        duplicate.toTpm(buf);
        buf.writeInt((inSymSeed!=null)?inSymSeed.length:0, 2);
        if(inSymSeed!=null)
            buf.write(inSymSeed);
        symmetricAlg.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        parentHandle = TPM_HANDLE.fromTpm(buf);
        int _encryptionKeySize = buf.readInt(2);
        encryptionKey = new byte[_encryptionKeySize];
        buf.readArrayOfInts(encryptionKey, 1, _encryptionKeySize);
        int _objectPublicSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _objectPublicSize));
        objectPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
        duplicate = TPM2B_PRIVATE.fromTpm(buf);
        int _inSymSeedSize = buf.readInt(2);
        inSymSeed = new byte[_inSymSeedSize];
        buf.readArrayOfInts(inSymSeed, 1, _inSymSeedSize);
        symmetricAlg = TPMT_SYM_DEF_OBJECT.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_Import_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Import_REQUEST ret = new TPM2_Import_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_Import_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Import_REQUEST ret = new TPM2_Import_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Import_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "parentHandle", parentHandle);
        _p.add(d, "byte", "encryptionKey", encryptionKey);
        _p.add(d, "TPMT_PUBLIC", "objectPublic", objectPublic);
        _p.add(d, "TPM2B_PRIVATE", "duplicate", duplicate);
        _p.add(d, "byte", "inSymSeed", inSymSeed);
        _p.add(d, "TPMT_SYM_DEF_OBJECT", "symmetricAlg", symmetricAlg);
    };
    
    
};

//<<<

