package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows the TPM to serve in the role as a Duplication Authority. If proper authorization for use of the oldParent is provided, then an HMAC key and a symmetric key are recovered from inSymSeed and used to integrity check and decrypt inDuplicate. A new protection seed value is generated according to the methods appropriate for newParent and the blob is re-encrypted and a new integrity value is computed. The re-encrypted blob is returned in outDuplicate and the symmetric key returned in outSymKey.
*/
public class RewrapResponse extends TpmStructure
{
    /**
     * This command allows the TPM to serve in the role as a Duplication Authority. If proper authorization for use of the oldParent is provided, then an HMAC key and a symmetric key are recovered from inSymSeed and used to integrity check and decrypt inDuplicate. A new protection seed value is generated according to the methods appropriate for newParent and the blob is re-encrypted and a new integrity value is computed. The re-encrypted blob is returned in outDuplicate and the symmetric key returned in outSymKey.
     * 
     * @param _outDuplicate an object encrypted using symmetric key derived from outSymSeed 
     * @param _outSymSeed seed for a symmetric key protected by newParent asymmetric key
     */
    public RewrapResponse(TPM2B_PRIVATE _outDuplicate,byte[] _outSymSeed)
    {
        outDuplicate = _outDuplicate;
        outSymSeed = _outSymSeed;
    }
    /**
    * This command allows the TPM to serve in the role as a Duplication Authority. If proper authorization for use of the oldParent is provided, then an HMAC key and a symmetric key are recovered from inSymSeed and used to integrity check and decrypt inDuplicate. A new protection seed value is generated according to the methods appropriate for newParent and the blob is re-encrypted and a new integrity value is computed. The re-encrypted blob is returned in outDuplicate and the symmetric key returned in outSymKey.
    */
    public RewrapResponse() {};
    /**
    * an object encrypted using symmetric key derived from outSymSeed
    */
    public TPM2B_PRIVATE outDuplicate;
    /**
    * size of the secret value
    */
    // private short outSymSeedSize;
    /**
    * seed for a symmetric key protected by newParent asymmetric key
    */
    public byte[] outSymSeed;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        outDuplicate.toTpm(buf);
        buf.writeInt((outSymSeed!=null)?outSymSeed.length:0, 2);
        if(outSymSeed!=null)
            buf.write(outSymSeed);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        outDuplicate = TPM2B_PRIVATE.fromTpm(buf);
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
    public static RewrapResponse fromTpm (byte[] x) 
    {
        RewrapResponse ret = new RewrapResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static RewrapResponse fromTpm (InByteBuf buf) 
    {
        RewrapResponse ret = new RewrapResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Rewrap_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM2B_PRIVATE", "outDuplicate", outDuplicate);
        _p.add(d, "byte", "outSymSeed", outSymSeed);
    };
    
    
};

//<<<

