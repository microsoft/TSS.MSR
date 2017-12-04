package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This list is used to report on a list of properties that are TPMS_PCR_SELECT values. It is returned by a TPM2_GetCapability().
*/
public class TPML_TAGGED_PCR_PROPERTY extends TpmStructure implements TPMU_CAPABILITIES 
{
    /**
     * This list is used to report on a list of properties that are TPMS_PCR_SELECT values. It is returned by a TPM2_GetCapability().
     * 
     * @param _pcrProperty a tagged PCR selection
     */
    public TPML_TAGGED_PCR_PROPERTY(TPMS_TAGGED_PCR_SELECT[] _pcrProperty)
    {
        pcrProperty = _pcrProperty;
    }
    /**
    * This list is used to report on a list of properties that are TPMS_PCR_SELECT values. It is returned by a TPM2_GetCapability().
    */
    public TPML_TAGGED_PCR_PROPERTY() {};
    /**
    * number of properties A value of zero is allowed.
    */
    // private int count;
    /**
    * a tagged PCR selection
    */
    public TPMS_TAGGED_PCR_SELECT[] pcrProperty;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((pcrProperty!=null)?pcrProperty.length:0, 4);
        if(pcrProperty!=null)
            buf.writeArrayOfTpmObjects(pcrProperty);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _count = buf.readInt(4);
        pcrProperty = new TPMS_TAGGED_PCR_SELECT[_count];
        for(int j=0;j<_count;j++)pcrProperty[j]=new TPMS_TAGGED_PCR_SELECT();
        buf.readArrayOfTpmObjects(pcrProperty, _count);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPML_TAGGED_PCR_PROPERTY fromTpm (byte[] x) 
    {
        TPML_TAGGED_PCR_PROPERTY ret = new TPML_TAGGED_PCR_PROPERTY();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPML_TAGGED_PCR_PROPERTY fromTpm (InByteBuf buf) 
    {
        TPML_TAGGED_PCR_PROPERTY ret = new TPML_TAGGED_PCR_PROPERTY();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_TAGGED_PCR_PROPERTY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_TAGGED_PCR_SELECT", "pcrProperty", pcrProperty);
    };
    
    
};

//<<<

