package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure contains the parameters for a symmetric block cipher object.
*/
public class TPMS_SYMCIPHER_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS 
{
    /**
     * This structure contains the parameters for a symmetric block cipher object.
     * 
     * @param _sym a symmetric block cipher
     */
    public TPMS_SYMCIPHER_PARMS(TPMT_SYM_DEF_OBJECT _sym)
    {
        sym = _sym;
    }
    /**
    * This structure contains the parameters for a symmetric block cipher object.
    */
    public TPMS_SYMCIPHER_PARMS() {};
    /**
    * a symmetric block cipher
    */
    public TPMT_SYM_DEF_OBJECT sym;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        sym.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        sym = TPMT_SYM_DEF_OBJECT.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_SYMCIPHER_PARMS fromTpm (byte[] x) 
    {
        TPMS_SYMCIPHER_PARMS ret = new TPMS_SYMCIPHER_PARMS();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_SYMCIPHER_PARMS fromTpm (InByteBuf buf) 
    {
        TPMS_SYMCIPHER_PARMS ret = new TPMS_SYMCIPHER_PARMS();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SYMCIPHER_PARMS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_SYM_DEF_OBJECT", "sym", sym);
    };
    
    
};

//<<<

