package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Handle of a loaded TPM key or other object [TSS]  */
public class TPM_HANDLE extends TpmStructure
{
    /** Handle value  */
    public int handle;
    
    public TPM_HANDLE() { handle = TPM_RH.NULL.toInt(); }
    
    /** @param _handle Handle value  */
    public TPM_HANDLE(int _handle) { handle = _handle; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeInt(handle); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { handle = buf.readInt(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM_HANDLE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM_HANDLE.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM_HANDLE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM_HANDLE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM_HANDLE.class);
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
        _p.add(d, "int", "handle", handle);
    }
    
    public static final TPM_HANDLE
        /** Represents TPM_RH.NULL handle constant */
        NULL = TPM_HANDLE.from(TPM_RH.NULL),
        /** Represents TPM_RH.NULL handle constant */
        PW = TPM_HANDLE.from(TPM_RH.PW),
        /** Represents TPM_RH.OWNER handle constant */
        OWNER = TPM_HANDLE.from(TPM_RH.OWNER),
        /** Represents TPM_RH.ENDORSEMENT handle constant */
        ENDORSEMENT = TPM_HANDLE.from(TPM_RH.ENDORSEMENT),
        /** Represents TPM_RH.PLATFORM handle constant */
        PLATFORM = TPM_HANDLE.from(TPM_RH.PLATFORM);
    
    /** Authorization value associated with this handle object.<BR>
     *  NOTE: It is tracked by the framework whenever possible but in some cases may be left uninitialized.
     */
    public byte[] AuthValue;
        
    /** Name of the TPM entity represented by this handle object.<BR>
     *  NOTE: It is tracked by the framework whenever possible but in some cases may be left uninitialized.
     */
    public byte[] Name;
    
    /** Wraps an arbitrary int value into a TPM handle object
     *  @param val  int value to be used as a TPM handle
     *  @return  New TPM_HANDLE object 
     */
    public static TPM_HANDLE from(int val)
    {
        return new TPM_HANDLE(val);
    }
    
    /** Creates a TPM handle from the given reserved handle constant
     *  @param _handle  Reserved handle constant
     *  @return  New TPM_HANDLE object 
     */
    public static TPM_HANDLE from(TPM_RH _handle)
    {
    	return new TPM_HANDLE(_handle.toInt());
    }
    
    /** @return  New NULL TPM handle */
    public static TPM_HANDLE nullHandle()
    {
    	return new TPM_HANDLE(TPM_RH.NULL.toInt());
    }
    
    /** Creates a pesistent TPM handle with the given offset (0 - 0x00FFFFFF)
     *  @param handleOffset  Reserved handle offset
     *  @return  New persistent TPM handle 
     */
    public static TPM_HANDLE persistent(int handleOffset)
    {
        return new TPM_HANDLE(((TPM_HT.PERSISTENT.toInt()) << 24) + handleOffset);
    };
    
    /** Creates a TPM handle for a PCR with the given index
     *  @param PcrIndex The PCR index (0 - 23)
     *  @return New TPM_HANDLE object
     */
    public static TPM_HANDLE pcr(int PcrIndex)
    {
        return new TPM_HANDLE(PcrIndex);
    }
    
    /** Creates a TPM_HANDLE for the given NV index
     *  @param  NvSlot  NV index
     *  @return New TPM handle object 
     */
    public static TPM_HANDLE NV(int NvSlot)
    {
        int handleVal = (TPM_HT.NV_INDEX.toInt() << 24) + NvSlot;
        return new TPM_HANDLE(handleVal);
    };
    
    /** @return  This handle type */
    public TPM_HT getType()
    {
        return TPM_HT.fromInt(handle >> 24);
    };
    
    /** Creates a password session handle with the given authorization value
     * @param authValue  Authorization value
     * @return  New session handle
     */
    public static TPM_HANDLE pwSession(byte[] authValue)
    {
        TPM_HANDLE pwapHandle = TPM_HANDLE.from(TPM_RH.PW);
        pwapHandle.AuthValue = authValue;
        return pwapHandle;
    }
    
    /** @return  The TPM name of this handle */
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
                if (Name.length == 0)
                    throw new RuntimeException("Name is not set for handle");
                return Name;
    
            default:
                throw new RuntimeException("Unknown handle type");
        }
    }
    
}

//<<<
