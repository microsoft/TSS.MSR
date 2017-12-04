package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Base class for empty union elements. An empty union element does not contain any data to marshal. This data structure can be used in place of any other union initialized with its own empty element.
*/
public class TPMS_NULL_UNION extends TpmStructure implements TPMU_SYM_KEY_BITS, TPMU_SYM_MODE, TPMU_SYM_DETAILS, TPMU_SCHEME_KEYEDHASH, TPMU_SIG_SCHEME, TPMU_KDF_SCHEME, TPMU_ASYM_SCHEME, TPMU_SIGNATURE 
{
    /**
     * Base class for empty union elements. An empty union element does not contain any data to marshal. This data structure can be used in place of any other union initialized with its own empty element.
     */
    public TPMS_NULL_UNION()
    {
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_NULL_UNION fromTpm (byte[] x) 
    {
        TPMS_NULL_UNION ret = new TPMS_NULL_UNION();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_NULL_UNION fromTpm (InByteBuf buf) 
    {
        TPMS_NULL_UNION ret = new TPMS_NULL_UNION();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NULL_UNION");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
    };
    
    
};

//<<<

