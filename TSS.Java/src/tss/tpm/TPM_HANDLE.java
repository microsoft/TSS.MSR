package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* TPM object handle (and related data)
*/
public class TPM_HANDLE extends TpmStructure
{
    /**
     * TPM object handle (and related data)
     * 
     * @param _handle TPM key handle
     */
    public TPM_HANDLE(int _handle)
    {
        handle = _handle;
    }
    /**
    * TPM object handle (and related data)
    */
    public TPM_HANDLE() {};
    /**
    * TPM key handle
    */
    public int handle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.write(handle);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle =  buf.readInt(4);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM_HANDLE fromTpm (byte[] x) 
    {
        TPM_HANDLE ret = new TPM_HANDLE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM_HANDLE fromTpm (InByteBuf buf) 
    {
        TPM_HANDLE ret = new TPM_HANDLE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM_HANDLE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "uint", "handle", handle);
    };
    
    /**
     * Represents TPM_RH.NULL handle constant 
     */
    public static final TPM_HANDLE NULL = TPM_HANDLE.from(TPM_RH.NULL);
    
    /**
        * Authorization value associated with this handle object.
        * NOTE: It is tracked by the framework whenever possible but in some cases may be left uninitialized.
        */
    public byte[] AuthValue;
        
    /**
        * Name of the TPM entity represented by this handle object.
        * NOTE: It is tracked by the framework whenever possible but in some cases may be left uninitialized.
        */
    public byte[] Name;
    
    /**
     * Creates a TPM handle from an arbitrary int value
     * 
     * @param val An int value to be used as a TPM handle
     * @return New TPM_HANDLE object 
     */
    public static TPM_HANDLE from(int val)
    {
        return new TPM_HANDLE(val);
    }
    
    /**
     * Creates a TPM handle from a reserved handle constant
     * 
     * @param _handle The reserved handle constant
     * @return New TPM_HANDLE object 
     */
    public static TPM_HANDLE from(TPM_RH _handle)
    {
    	return new TPM_HANDLE(_handle.toInt());
    }
    
    /**
     * Creates a TPM_RH.NULL TPM_HANDLE 
     * @return The new TPM_HANDLE 
     */
    public static TPM_HANDLE nullHandle()
    {
    	return new TPM_HANDLE(TPM_RH.NULL.toInt());
    }
    
    /**
     * Creates a TPM_HANDLE from an offset into the reserved handle space
     * 
     * @param handleOffset The reserved handle offset
     * @return The new TPM_HANDLE 
     */
    public static TPM_HANDLE persistent(int handleOffset)
    {
        return new TPM_HANDLE(((TPM_HT.PERSISTENT.toInt()) << 24) + handleOffset);
    };
    
    /**
     * Creates a TPM_HANDLE for a PCR
     * 
     * @param PcrIndex The PCR index
     * @return The new TPM_HANDLE 
     */
    public static TPM_HANDLE pcr(int PcrIndex)
    {
        return new TPM_HANDLE(PcrIndex);
    }
    
    /**
     * Gets the handle type
     * 
     * @return The handle type
     */
    
    public TPM_HT getType()
    {
        return TPM_HT.fromInt(handle >> 24);
    };
    
    /**
     * Creates a TPM_HANDLE for an NV slot
     * 
     * @param NvSlot The NV index
     * @return The new TPM_HANDLE 
     */
    public static TPM_HANDLE NV(int NvSlot)
    {
        int handleVal = (TPM_HT.NV_INDEX.toInt() << 24) + NvSlot;
        return new TPM_HANDLE(handleVal);
    };
    
    /**
     * Creates a password session handle with the associated authorization value
     * 
     * @param authValue The authorization value
     * @return The new TPM_HANDLE 
     */
    public static TPM_HANDLE pwSession(byte[] authValue)
    {
        TPM_HANDLE pwapHandle = TPM_HANDLE.from(TPM_RH.RS_PW);
        pwapHandle.AuthValue = authValue;
        return pwapHandle;
    }
    
    /**
     * Gets the TPM-name associated with this handle
     * 
     * @return The name
     */
    public byte[] getName()
    {
        int handleType = getType().toInt();
    
        switch (handleType) {
            case 0:
            case 2:
            case 3:
            case 0x40:
                Name = Helpers.hostToNet(handle);
                return Name;
    
            case 1:
            case 0x80:
            case 0x81:
                if (Name.length == 0) {
                    throw new RuntimeException("Name is not set for handle");
                }
    
                return Name;
    
            default:
                throw new RuntimeException("Unknown handle type");
        }
    }
    
    
    
};

//<<<

