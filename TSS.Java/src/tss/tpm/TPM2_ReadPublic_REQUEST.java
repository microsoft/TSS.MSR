package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows access to the public area of a loaded object.
*/
public class TPM2_ReadPublic_REQUEST extends TpmStructure
{
    /**
     * This command allows access to the public area of a loaded object.
     * 
     * @param _objectHandle TPM handle of an object Auth Index: None
     */
    public TPM2_ReadPublic_REQUEST(TPM_HANDLE _objectHandle)
    {
        objectHandle = _objectHandle;
    }
    /**
    * This command allows access to the public area of a loaded object.
    */
    public TPM2_ReadPublic_REQUEST() {};
    /**
    * TPM handle of an object Auth Index: None
    */
    public TPM_HANDLE objectHandle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        objectHandle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        objectHandle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ReadPublic_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ReadPublic_REQUEST ret = new TPM2_ReadPublic_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ReadPublic_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ReadPublic_REQUEST ret = new TPM2_ReadPublic_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ReadPublic_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
    };
    
    
};

//<<<

