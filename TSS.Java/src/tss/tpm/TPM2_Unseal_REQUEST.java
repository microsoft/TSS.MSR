package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns the data in a loaded Sealed Data Object.
*/
public class TPM2_Unseal_REQUEST extends TpmStructure
{
    /**
     * This command returns the data in a loaded Sealed Data Object.
     * 
     * @param _itemHandle handle of a loaded data object Auth Index: 1 Auth Role: USER
     */
    public TPM2_Unseal_REQUEST(TPM_HANDLE _itemHandle)
    {
        itemHandle = _itemHandle;
    }
    /**
    * This command returns the data in a loaded Sealed Data Object.
    */
    public TPM2_Unseal_REQUEST() {};
    /**
    * handle of a loaded data object Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE itemHandle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        itemHandle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        itemHandle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_Unseal_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Unseal_REQUEST ret = new TPM2_Unseal_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_Unseal_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Unseal_REQUEST ret = new TPM2_Unseal_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Unseal_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "itemHandle", itemHandle);
    };
    
    
};

//<<<

