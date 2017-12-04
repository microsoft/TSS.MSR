package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command starts a hash or an Event Sequence. If hashAlg is an implemented hash, then a hash sequence is started. If hashAlg is TPM_ALG_NULL, then an Event Sequence is started. If hashAlg is neither an implemented algorithm nor TPM_ALG_NULL, then the TPM shall return TPM_RC_HASH.
*/
public class TPM2_HashSequenceStart_REQUEST extends TpmStructure
{
    /**
     * This command starts a hash or an Event Sequence. If hashAlg is an implemented hash, then a hash sequence is started. If hashAlg is TPM_ALG_NULL, then an Event Sequence is started. If hashAlg is neither an implemented algorithm nor TPM_ALG_NULL, then the TPM shall return TPM_RC_HASH.
     * 
     * @param _auth authorization value for subsequent use of the sequence 
     * @param _hashAlg the hash algorithm to use for the hash sequence An Event Sequence starts if this is TPM_ALG_NULL.
     */
    public TPM2_HashSequenceStart_REQUEST(byte[] _auth,TPM_ALG_ID _hashAlg)
    {
        auth = _auth;
        hashAlg = _hashAlg;
    }
    /**
    * This command starts a hash or an Event Sequence. If hashAlg is an implemented hash, then a hash sequence is started. If hashAlg is TPM_ALG_NULL, then an Event Sequence is started. If hashAlg is neither an implemented algorithm nor TPM_ALG_NULL, then the TPM shall return TPM_RC_HASH.
    */
    public TPM2_HashSequenceStart_REQUEST() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short authSize;
    /**
    * authorization value for subsequent use of the sequence
    */
    public byte[] auth;
    /**
    * the hash algorithm to use for the hash sequence An Event Sequence starts if this is TPM_ALG_NULL.
    */
    public TPM_ALG_ID hashAlg;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((auth!=null)?auth.length:0, 2);
        if(auth!=null)
            buf.write(auth);
        hashAlg.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _authSize = buf.readInt(2);
        auth = new byte[_authSize];
        buf.readArrayOfInts(auth, 1, _authSize);
        hashAlg = TPM_ALG_ID.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_HashSequenceStart_REQUEST fromTpm (byte[] x) 
    {
        TPM2_HashSequenceStart_REQUEST ret = new TPM2_HashSequenceStart_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_HashSequenceStart_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_HashSequenceStart_REQUEST ret = new TPM2_HashSequenceStart_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HashSequenceStart_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "auth", auth);
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
    };
    
    
};

//<<<

