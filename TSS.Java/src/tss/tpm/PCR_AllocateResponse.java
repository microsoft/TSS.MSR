package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to set the desired PCR allocation of PCR and algorithms. This command requires Platform Authorization.
*/
public class PCR_AllocateResponse extends TpmStructure
{
    /**
     * This command is used to set the desired PCR allocation of PCR and algorithms. This command requires Platform Authorization.
     * 
     * @param _allocationSuccess YES if the allocation succeeded 
     * @param _maxPCR maximum number of PCR that may be in a bank 
     * @param _sizeNeeded number of octets required to satisfy the request 
     * @param _sizeAvailable Number of octets available. Computed before the allocation.
     */
    public PCR_AllocateResponse(byte _allocationSuccess,int _maxPCR,int _sizeNeeded,int _sizeAvailable)
    {
        allocationSuccess = _allocationSuccess;
        maxPCR = _maxPCR;
        sizeNeeded = _sizeNeeded;
        sizeAvailable = _sizeAvailable;
    }
    /**
    * This command is used to set the desired PCR allocation of PCR and algorithms. This command requires Platform Authorization.
    */
    public PCR_AllocateResponse() {};
    /**
    * YES if the allocation succeeded
    */
    public byte allocationSuccess;
    /**
    * maximum number of PCR that may be in a bank
    */
    public int maxPCR;
    /**
    * number of octets required to satisfy the request
    */
    public int sizeNeeded;
    /**
    * Number of octets available. Computed before the allocation.
    */
    public int sizeAvailable;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.write(allocationSuccess);
        buf.write(maxPCR);
        buf.write(sizeNeeded);
        buf.write(sizeAvailable);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        allocationSuccess = (byte) buf.readInt(1);
        maxPCR =  buf.readInt(4);
        sizeNeeded =  buf.readInt(4);
        sizeAvailable =  buf.readInt(4);
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
        _p.add(d, "BYTE", "allocationSuccess", allocationSuccess);
        _p.add(d, "uint", "maxPCR", maxPCR);
        _p.add(d, "uint", "sizeNeeded", sizeNeeded);
        _p.add(d, "uint", "sizeAvailable", sizeAvailable);
    };
    
    
};

//<<<

