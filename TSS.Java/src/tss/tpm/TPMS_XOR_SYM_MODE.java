package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Custom data structure representing an empty element (i.e. the one with no data to marshal) for selector algorithm TPM_ALG_XOR for the union TpmuSymMode
*/
public class TPMS_XOR_SYM_MODE extends TpmStructure implements TPMU_SYM_KEY_BITS, TPMU_SYM_MODE, TPMU_SYM_DETAILS, TPMU_SCHEME_KEYEDHASH, TPMU_SIG_SCHEME, TPMU_KDF_SCHEME, TPMU_ASYM_SCHEME, TPMU_SIGNATURE 
{
    /**
     * Custom data structure representing an empty element (i.e. the one with no data to marshal) for selector algorithm TPM_ALG_XOR for the union TpmuSymMode
     */
    public TPMS_XOR_SYM_MODE()
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
    public static TPMS_XOR_SYM_MODE fromTpm (byte[] x) 
    {
        TPMS_XOR_SYM_MODE ret = new TPMS_XOR_SYM_MODE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_XOR_SYM_MODE fromTpm (InByteBuf buf) 
    {
        TPMS_XOR_SYM_MODE ret = new TPMS_XOR_SYM_MODE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_XOR_SYM_MODE");
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

