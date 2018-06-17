package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* The TPMT_SYM_DEF structure is used to select an algorithm to be used for parameter encryption in those cases when different symmetric algorithms may be selected.
*/
public class TPMT_SYM_DEF extends TpmStructure
{
    /**
     * The TPMT_SYM_DEF structure is used to select an algorithm to be used for parameter encryption in those cases when different symmetric algorithms may be selected.
     * 
     * @param _algorithm symmetric algorithm 
     * @param _keyBits key size in bits 
     * @param _mode encryption mode
     */
    public TPMT_SYM_DEF(TPM_ALG_ID _algorithm,int _keyBits,TPM_ALG_ID _mode)
    {
        algorithm = _algorithm;
        keyBits = (short)_keyBits;
        mode = _mode;
    }
    /**
    * The TPMT_SYM_DEF structure is used to select an algorithm to be used for parameter encryption in those cases when different symmetric algorithms may be selected.
    */
    public TPMT_SYM_DEF() {};
    /**
    * symmetric algorithm
    */
    public TPM_ALG_ID algorithm;
    /**
    * key size in bits
    */
    public short keyBits;
    /**
    * encryption mode
    */
    public TPM_ALG_ID mode;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        Helpers.nonDefaultMarshallOut(buf, this);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        Helpers.nonDefaultMarshallIn(buf, this);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMT_SYM_DEF fromTpm (byte[] x) 
    {
        TPMT_SYM_DEF ret = new TPMT_SYM_DEF();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMT_SYM_DEF fromTpm (InByteBuf buf) 
    {
        TPMT_SYM_DEF ret = new TPMT_SYM_DEF();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SYM_DEF");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "algorithm", algorithm);
        _p.add(d, "UINT16", "keyBits", keyBits);
        _p.add(d, "TPM_ALG_ID", "mode", mode);
    };
    
    /**
    * Create a NULL TPMT_SYM_DEF object
    * 
    * @return The null object
    */
    public static TPMT_SYM_DEF nullObject()
    {
     	return new TPMT_SYM_DEF(
    								TPM_ALG_ID.NULL, 
    								(short) 0, 
    								TPM_ALG_ID.NULL
    								);
    }
    
    
};

//<<<

