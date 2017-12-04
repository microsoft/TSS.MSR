package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Most of the ECC signature schemes only require a hash algorithm to complete the definition and can be typed as TPMS_SCHEME_HASH. Anonymous algorithms also require a count value so they are typed to be TPMS_SCHEME_ECDAA.
*/
public class TPMS_SIG_SCHEME_ECDAA extends TpmStructure implements TPMU_SIG_SCHEME, TPMU_ASYM_SCHEME 
{
    /**
     * Most of the ECC signature schemes only require a hash algorithm to complete the definition and can be typed as TPMS_SCHEME_HASH. Anonymous algorithms also require a count value so they are typed to be TPMS_SCHEME_ECDAA.
     * 
     * @param _hashAlg the hash algorithm used to digest the message 
     * @param _count the counter value that is used between TPM2_Commit() and the sign operation
     */
    public TPMS_SIG_SCHEME_ECDAA(TPM_ALG_ID _hashAlg,int _count)
    {
        hashAlg = _hashAlg;
        count = (short)_count;
    }
    /**
    * Most of the ECC signature schemes only require a hash algorithm to complete the definition and can be typed as TPMS_SCHEME_HASH. Anonymous algorithms also require a count value so they are typed to be TPMS_SCHEME_ECDAA.
    */
    public TPMS_SIG_SCHEME_ECDAA() {};
    /**
    * the hash algorithm used to digest the message
    */
    public TPM_ALG_ID hashAlg;
    /**
    * the counter value that is used between TPM2_Commit() and the sign operation
    */
    public short count;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        hashAlg.toTpm(buf);
        buf.write(count);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        count = (short) buf.readInt(2);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_SIG_SCHEME_ECDAA fromTpm (byte[] x) 
    {
        TPMS_SIG_SCHEME_ECDAA ret = new TPMS_SIG_SCHEME_ECDAA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_SIG_SCHEME_ECDAA fromTpm (InByteBuf buf) 
    {
        TPMS_SIG_SCHEME_ECDAA ret = new TPMS_SIG_SCHEME_ECDAA();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIG_SCHEME_ECDAA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
        _p.add(d, "ushort", "count", count);
    };
    
    
};

//<<<

