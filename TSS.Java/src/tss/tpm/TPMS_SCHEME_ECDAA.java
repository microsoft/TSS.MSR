package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This definition is for split signing schemes that require a commit count. */
public class TPMS_SCHEME_ECDAA extends TpmStructure implements TPMU_SIG_SCHEME, TPMU_ASYM_SCHEME
{
    /** the hash algorithm used to digest the message */
    public TPM_ALG_ID hashAlg;
    
    /** the counter value that is used between TPM2_Commit() and the sign operation */
    public short count;
    
    public TPMS_SCHEME_ECDAA() { hashAlg = TPM_ALG_ID.NULL; }
    
    /**
     *  @param _hashAlg the hash algorithm used to digest the message
     *  @param _count the counter value that is used between TPM2_Commit() and the sign operation
     */
    public TPMS_SCHEME_ECDAA(TPM_ALG_ID _hashAlg, int _count)
    {
        hashAlg = _hashAlg;
        count = (short)_count;
    }

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ECDAA; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        hashAlg.toTpm(buf);
        buf.writeShort(count);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        count = buf.readShort();
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPMS_SCHEME_ECDAA fromTpm (byte[] x) 
    {
        TPMS_SCHEME_ECDAA ret = new TPMS_SCHEME_ECDAA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_SCHEME_ECDAA fromTpm (InByteBuf buf) 
    {
        TPMS_SCHEME_ECDAA ret = new TPMS_SCHEME_ECDAA();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_ECDAA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
        _p.add(d, "short", "count", count);
    }
}

//<<<
