package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns the values of all PCR specified in pcrSelectionIn.
*/
public class TPM2_PCR_Read_REQUEST extends TpmStructure
{
    /**
     * This command returns the values of all PCR specified in pcrSelectionIn.
     * 
     * @param _pcrSelectionIn The selection of PCR to read
     */
    public TPM2_PCR_Read_REQUEST(TPMS_PCR_SELECTION[] _pcrSelectionIn)
    {
        pcrSelectionIn = _pcrSelectionIn;
    }
    /**
    * This command returns the values of all PCR specified in pcrSelectionIn.
    */
    public TPM2_PCR_Read_REQUEST() {};
    /**
    * number of selection structures A value of zero is allowed.
    */
    // private int pcrSelectionInCount;
    /**
    * The selection of PCR to read
    */
    public TPMS_PCR_SELECTION[] pcrSelectionIn;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((pcrSelectionIn!=null)?pcrSelectionIn.length:0, 4);
        if(pcrSelectionIn!=null)
            buf.writeArrayOfTpmObjects(pcrSelectionIn);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _pcrSelectionInCount = buf.readInt(4);
        pcrSelectionIn = new TPMS_PCR_SELECTION[_pcrSelectionInCount];
        for(int j=0;j<_pcrSelectionInCount;j++)pcrSelectionIn[j]=new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(pcrSelectionIn, _pcrSelectionInCount);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PCR_Read_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PCR_Read_REQUEST ret = new TPM2_PCR_Read_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PCR_Read_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PCR_Read_REQUEST ret = new TPM2_PCR_Read_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Read_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_PCR_SELECTION", "pcrSelectionIn", pcrSelectionIn);
    };
    
    
};

//<<<

