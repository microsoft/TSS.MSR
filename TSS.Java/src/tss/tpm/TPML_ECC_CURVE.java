package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This list is used to report the ECC curve ID values supported by the TPM. It is returned by a TPM2_GetCapability().
*/
public class TPML_ECC_CURVE extends TpmStructure implements TPMU_CAPABILITIES 
{
    /**
     * This list is used to report the ECC curve ID values supported by the TPM. It is returned by a TPM2_GetCapability().
     * 
     * @param _eccCurves array of ECC curve identifiers
     */
    public TPML_ECC_CURVE(TPM_ECC_CURVE[] _eccCurves)
    {
        eccCurves = _eccCurves;
    }
    /**
    * This list is used to report the ECC curve ID values supported by the TPM. It is returned by a TPM2_GetCapability().
    */
    public TPML_ECC_CURVE() {};
    /**
    * number of curves A value of zero is allowed.
    */
    // private int count;
    /**
    * array of ECC curve identifiers
    */
    public TPM_ECC_CURVE[] eccCurves;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((eccCurves!=null)?eccCurves.length:0, 4);
        if(eccCurves!=null)
            buf.writeArrayOfTpmObjects(eccCurves);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _count = buf.readInt(4);
        eccCurves = new TPM_ECC_CURVE[_count];
        for(int j=0;j<_count;j++){eccCurves[j]=TPM_ECC_CURVE.fromTpm(buf);};
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPML_ECC_CURVE fromTpm (byte[] x) 
    {
        TPML_ECC_CURVE ret = new TPML_ECC_CURVE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPML_ECC_CURVE fromTpm (InByteBuf buf) 
    {
        TPML_ECC_CURVE ret = new TPML_ECC_CURVE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_ECC_CURVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ECC_CURVE", "eccCurves", eccCurves);
    };
    
    
};

//<<<

