package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to change the authorization secret for a TPM-resident object.
*/
public class ObjectChangeAuthResponse extends TpmStructure
{
    /**
     * This command is used to change the authorization secret for a TPM-resident object.
     * 
     * @param _outPrivate private area containing the new authorization value
     */
    public ObjectChangeAuthResponse(TPM2B_PRIVATE _outPrivate)
    {
        outPrivate = _outPrivate;
    }
    /**
    * This command is used to change the authorization secret for a TPM-resident object.
    */
    public ObjectChangeAuthResponse() {};
    /**
    * private area containing the new authorization value
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
    public static ObjectChangeAuthResponse fromTpm (byte[] x) 
    {
        ObjectChangeAuthResponse ret = new ObjectChangeAuthResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ObjectChangeAuthResponse fromTpm (InByteBuf buf) 
    {
        ObjectChangeAuthResponse ret = new ObjectChangeAuthResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ObjectChangeAuth_RESPONSE");
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

