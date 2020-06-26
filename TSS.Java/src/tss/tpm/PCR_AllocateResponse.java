package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to set the desired PCR allocation of PCR and algorithms. This
 *  command requires Platform Authorization.
 */
public class PCR_AllocateResponse extends TpmStructure
{
    /** YES if the allocation succeeded  */
    public byte allocationSuccess;
    
    /** Maximum number of PCR that may be in a bank  */
    public int maxPCR;
    
    /** Number of octets required to satisfy the request  */
    public int sizeNeeded;
    
    /** Number of octets available. Computed before the allocation.  */
    public int sizeAvailable;
    
    public PCR_AllocateResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeByte(allocationSuccess);
        buf.writeInt(maxPCR);
        buf.writeInt(sizeNeeded);
        buf.writeInt(sizeAvailable);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        allocationSuccess = buf.readByte();
        maxPCR = buf.readInt();
        sizeNeeded = buf.readInt();
        sizeAvailable = buf.readInt();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static PCR_AllocateResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(PCR_AllocateResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static PCR_AllocateResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static PCR_AllocateResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(PCR_AllocateResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Allocate_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "allocationSuccess", allocationSuccess);
        _p.add(d, "int", "maxPCR", maxPCR);
        _p.add(d, "int", "sizeNeeded", sizeNeeded);
        _p.add(d, "int", "sizeAvailable", sizeAvailable);
    }
}

//<<<
