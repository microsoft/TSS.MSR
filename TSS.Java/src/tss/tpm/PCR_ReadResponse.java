package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns the values of all PCR specified in pcrSelectionIn.
*/
public class PCR_ReadResponse extends TpmStructure
{
    /**
     * This command returns the values of all PCR specified in pcrSelectionIn.
     * 
     * @param _pcrUpdateCounter the current value of the PCR update counter 
     * @param _pcrSelectionOut the PCR in the returned list 
     * @param _pcrValues the contents of the PCR indicated in pcrSelectOut-) pcrSelection[] as tagged digests
     */
    public PCR_ReadResponse(int _pcrUpdateCounter,TPMS_PCR_SELECTION[] _pcrSelectionOut,TPM2B_DIGEST[] _pcrValues)
    {
        pcrUpdateCounter = _pcrUpdateCounter;
        pcrSelectionOut = _pcrSelectionOut;
        pcrValues = _pcrValues;
    }
    /**
    * This command returns the values of all PCR specified in pcrSelectionIn.
    */
    public PCR_ReadResponse() {};
    /**
    * the current value of the PCR update counter
    */
    public int pcrUpdateCounter;
    /**
    * number of selection structures A value of zero is allowed.
    */
    // private int pcrSelectionOutCount;
    /**
    * the PCR in the returned list
    */
    public TPMS_PCR_SELECTION[] pcrSelectionOut;
    /**
    * number of digests in the list, minimum is two for TPM2_PolicyOR().
    */
    // private int pcrValuesCount;
    /**
    * the contents of the PCR indicated in pcrSelectOut-) pcrSelection[] as tagged digests
    */
    public TPM2B_DIGEST[] pcrValues;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.write(pcrUpdateCounter);
        buf.writeInt((pcrSelectionOut!=null)?pcrSelectionOut.length:0, 4);
        if(pcrSelectionOut!=null)
            buf.writeArrayOfTpmObjects(pcrSelectionOut);
        buf.writeInt((pcrValues!=null)?pcrValues.length:0, 4);
        if(pcrValues!=null)
            buf.writeArrayOfTpmObjects(pcrValues);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        pcrUpdateCounter =  buf.readInt(4);
        int _pcrSelectionOutCount = buf.readInt(4);
        pcrSelectionOut = new TPMS_PCR_SELECTION[_pcrSelectionOutCount];
        for(int j=0;j<_pcrSelectionOutCount;j++)pcrSelectionOut[j]=new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(pcrSelectionOut, _pcrSelectionOutCount);
        int _pcrValuesCount = buf.readInt(4);
        pcrValues = new TPM2B_DIGEST[_pcrValuesCount];
        for(int j=0;j<_pcrValuesCount;j++)pcrValues[j]=new TPM2B_DIGEST();
        buf.readArrayOfTpmObjects(pcrValues, _pcrValuesCount);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static PCR_ReadResponse fromTpm (byte[] x) 
    {
        PCR_ReadResponse ret = new PCR_ReadResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PCR_ReadResponse fromTpm (InByteBuf buf) 
    {
        PCR_ReadResponse ret = new PCR_ReadResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Read_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "uint", "pcrUpdateCounter", pcrUpdateCounter);
        _p.add(d, "TPMS_PCR_SELECTION", "pcrSelectionOut", pcrSelectionOut);
        _p.add(d, "TPM2B_DIGEST", "pcrValues", pcrValues);
    };
    
    
};

//<<<

