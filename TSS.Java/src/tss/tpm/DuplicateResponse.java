package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command duplicates a loaded object so that it may be used in a different hierarchy. The new parent key for the duplicate may be on the same or different TPM or TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
*/
public class DuplicateResponse extends TpmStructure
{
    /**
     * This command duplicates a loaded object so that it may be used in a different hierarchy. The new parent key for the duplicate may be on the same or different TPM or TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
     * 
     * @param _encryptionKeyOut If the caller provided an encryption key or if symmetricAlg was TPM_ALG_NULL, then this will be the Empty Buffer; otherwise, it shall contain the TPM-generated, symmetric encryption key for the inner wrapper. 
     * @param _duplicate private area that may be encrypted by encryptionKeyIn; and may be doubly encrypted 
     * @param _outSymSeed seed protected by the asymmetric algorithms of new parent (NP)
     */
    public DuplicateResponse(byte[] _encryptionKeyOut,TPM2B_PRIVATE _duplicate,byte[] _outSymSeed)
    {
        encryptionKeyOut = _encryptionKeyOut;
        duplicate = _duplicate;
        outSymSeed = _outSymSeed;
    }
    /**
    * This command duplicates a loaded object so that it may be used in a different hierarchy. The new parent key for the duplicate may be on the same or different TPM or TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
    */
    public DuplicateResponse() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short encryptionKeyOutSize;
    /**
    * If the caller provided an encryption key or if symmetricAlg was TPM_ALG_NULL, then this will be the Empty Buffer; otherwise, it shall contain the TPM-generated, symmetric encryption key for the inner wrapper.
    */
    public byte[] encryptionKeyOut;
    /**
    * private area that may be encrypted by encryptionKeyIn; and may be doubly encrypted
    */
    public TPM2B_PRIVATE duplicate;
    /**
    * size of the secret value
    */
    // private short outSymSeedSize;
    /**
    * seed protected by the asymmetric algorithms of new parent (NP)
    */
    public byte[] outSymSeed;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((encryptionKeyOut!=null)?encryptionKeyOut.length:0, 2);
        if(encryptionKeyOut!=null)
            buf.write(encryptionKeyOut);
        duplicate.toTpm(buf);
        buf.writeInt((outSymSeed!=null)?outSymSeed.length:0, 2);
        if(outSymSeed!=null)
            buf.write(outSymSeed);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _encryptionKeyOutSize = buf.readInt(2);
        encryptionKeyOut = new byte[_encryptionKeyOutSize];
        buf.readArrayOfInts(encryptionKeyOut, 1, _encryptionKeyOutSize);
        duplicate = TPM2B_PRIVATE.fromTpm(buf);
        int _outSymSeedSize = buf.readInt(2);
        outSymSeed = new byte[_outSymSeedSize];
        buf.readArrayOfInts(outSymSeed, 1, _outSymSeedSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static DuplicateResponse fromTpm (byte[] x) 
    {
        DuplicateResponse ret = new DuplicateResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static DuplicateResponse fromTpm (InByteBuf buf) 
    {
        DuplicateResponse ret = new DuplicateResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Duplicate_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "encryptionKeyOut", encryptionKeyOut);
        _p.add(d, "TPM2B_PRIVATE", "duplicate", duplicate);
        _p.add(d, "byte", "outSymSeed", outSymSeed);
    };
    
    
};

//<<<

