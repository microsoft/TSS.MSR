package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command is used to set the desired PCR allocation of PCR and algorithms. This command
 *  requires Platform Authorization.
 */
public class PCR_AllocateResponse extends TpmStructure
{
    /** YES if the allocation succeeded */
    public byte allocationSuccess;
    
    /** maximum number of PCR that may be in a bank */
    public int maxPCR;
    
    /** number of octets required to satisfy the request */
    public int sizeNeeded;
    
    /** Number of octets available. Computed before the allocation. */
    public int sizeAvailable;
    
    public PCR_AllocateResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeByte(allocationSuccess);
        buf.writeInt(maxPCR);
        buf.writeInt(sizeNeeded);
        buf.writeInt(sizeAvailable);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        allocationSuccess = buf.readByte();
        maxPCR = buf.readInt();
        sizeNeeded = buf.readInt();
        sizeAvailable = buf.readInt();
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static PCR_AllocateResponse fromTpm (byte[] x) 
    {
        PCR_AllocateResponse ret = new PCR_AllocateResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static PCR_AllocateResponse fromTpm (InByteBuf buf) 
    {
        PCR_AllocateResponse ret = new PCR_AllocateResponse();
        ret.initFromTpm(buf);
        return ret;
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
