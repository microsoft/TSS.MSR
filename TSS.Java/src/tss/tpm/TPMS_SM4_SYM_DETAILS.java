package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  Custom data structure representing an empty element (i.e. the one with 
 *  no data to marshal) for selector algorithm TPM_ALG_SM4 for the union TPMU_SYM_DETAILS
 */
public class TPMS_SM4_SYM_DETAILS extends TPMS_NULL_UNION
{
    public TPMS_SM4_SYM_DETAILS() {}
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPMS_SM4_SYM_DETAILS fromTpm (byte[] x) 
    {
        TPMS_SM4_SYM_DETAILS ret = new TPMS_SM4_SYM_DETAILS();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_SM4_SYM_DETAILS fromTpm (InByteBuf buf) 
    {
        TPMS_SM4_SYM_DETAILS ret = new TPMS_SM4_SYM_DETAILS();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SM4_SYM_DETAILS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
    }
}

//<<<

