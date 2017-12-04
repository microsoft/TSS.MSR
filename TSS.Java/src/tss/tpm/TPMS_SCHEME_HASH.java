package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is the scheme data for schemes that only require a hash to complete their definition.
*/
public class TPMS_SCHEME_HASH extends TpmStructure implements TPMU_SCHEME_KEYEDHASH, TPMU_SIG_SCHEME, TPMU_KDF_SCHEME, TPMU_ASYM_SCHEME, TPMU_SIGNATURE 
{
    /**
     * This structure is the scheme data for schemes that only require a hash to complete their definition.
     * 
     * @param _hashAlg the hash algorithm used to digest the message
     */
    public TPMS_SCHEME_HASH(TPM_ALG_ID _hashAlg)
    {
        hashAlg = _hashAlg;
    }
    /**
    * This structure is the scheme data for schemes that only require a hash to complete their definition.
    */
    public TPMS_SCHEME_HASH() {};
    /**
    * the hash algorithm used to digest the message
    */
    public TPM_ALG_ID hashAlg;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        hashAlg.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        hashAlg = TPM_ALG_ID.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_SCHEME_HASH fromTpm (byte[] x) 
    {
        TPMS_SCHEME_HASH ret = new TPMS_SCHEME_HASH();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_SCHEME_HASH fromTpm (InByteBuf buf) 
    {
        TPMS_SCHEME_HASH ret = new TPMS_SCHEME_HASH();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_HASH");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
    };
    
    
};

//<<<

