package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
*/
public class ImportResponse extends TpmStructure
{
    /**
     * This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
     * 
     * @param _outPrivate the sensitive area encrypted with the symmetric key of parentHandle
     */
    public ImportResponse(TPM2B_PRIVATE _outPrivate)
    {
        outPrivate = _outPrivate;
    }
    /**
    * This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
    */
    public ImportResponse() {};
    /**
    * the sensitive area encrypted with the symmetric key of parentHandle
    */
    public TPM2B_PRIVATE outPrivate;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        outPrivate.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        outPrivate = TPM2B_PRIVATE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static ImportResponse fromTpm (byte[] x) 
    {
        ImportResponse ret = new ImportResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ImportResponse fromTpm (InByteBuf buf) 
    {
        ImportResponse ret = new ImportResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Import_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM2B_PRIVATE", "outPrivate", outPrivate);
    };
    
    
};

//<<<

