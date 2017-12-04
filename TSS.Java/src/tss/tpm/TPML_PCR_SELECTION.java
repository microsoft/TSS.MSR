package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This list is used to indicate the PCR that are included in a selection when more than one PCR value may be selected.
*/
public class TPML_PCR_SELECTION extends TpmStructure implements TPMU_CAPABILITIES 
{
    /**
     * This list is used to indicate the PCR that are included in a selection when more than one PCR value may be selected.
     * 
     * @param _pcrSelections list of selections
     */
    public TPML_PCR_SELECTION(TPMS_PCR_SELECTION[] _pcrSelections)
    {
        pcrSelections = _pcrSelections;
    }
    /**
    * This list is used to indicate the PCR that are included in a selection when more than one PCR value may be selected.
    */
    public TPML_PCR_SELECTION() {};
    /**
    * number of selection structures A value of zero is allowed.
    */
    // private int count;
    /**
    * list of selections
    */
    public TPMS_PCR_SELECTION[] pcrSelections;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((pcrSelections!=null)?pcrSelections.length:0, 4);
        if(pcrSelections!=null)
            buf.writeArrayOfTpmObjects(pcrSelections);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _count = buf.readInt(4);
        pcrSelections = new TPMS_PCR_SELECTION[_count];
        for(int j=0;j<_count;j++)pcrSelections[j]=new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(pcrSelections, _count);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPML_PCR_SELECTION fromTpm (byte[] x) 
    {
        TPML_PCR_SELECTION ret = new TPML_PCR_SELECTION();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPML_PCR_SELECTION fromTpm (InByteBuf buf) 
    {
        TPML_PCR_SELECTION ret = new TPML_PCR_SELECTION();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_PCR_SELECTION");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_PCR_SELECTION", "pcrSelections", pcrSelections);
    };
    
    
};

//<<<

