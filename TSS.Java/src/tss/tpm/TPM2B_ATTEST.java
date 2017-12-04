package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This sized buffer to contain the signed structure. The attestationData is the signed portion of the structure. The size parameter is not signed.
*/
public class TPM2B_ATTEST extends TpmStructure
{
    /**
     * This sized buffer to contain the signed structure. The attestationData is the signed portion of the structure. The size parameter is not signed.
     * 
     * @param _attestationData the signed structure
     */
    public TPM2B_ATTEST(TPMS_ATTEST _attestationData)
    {
        attestationData = _attestationData;
    }
    /**
    * This sized buffer to contain the signed structure. The attestationData is the signed portion of the structure. The size parameter is not signed.
    */
    public TPM2B_ATTEST() {};
    /**
    * size of the attestationData structure
    */
    // private short size;
    /**
    * the signed structure
    */
    public TPMS_ATTEST attestationData;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((attestationData!=null)?attestationData.toTpm().length:0, 2);
        if(attestationData!=null)
            attestationData.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _size));
        attestationData = TPMS_ATTEST.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2B_ATTEST fromTpm (byte[] x) 
    {
        TPM2B_ATTEST ret = new TPM2B_ATTEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_ATTEST fromTpm (InByteBuf buf) 
    {
        TPM2B_ATTEST ret = new TPM2B_ATTEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_ATTEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ATTEST", "attestationData", attestationData);
    };
    
    
};

//<<<

