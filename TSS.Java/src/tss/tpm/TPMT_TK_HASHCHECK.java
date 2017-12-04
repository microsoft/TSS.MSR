package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This ticket is produced by TPM2_SequenceComplete() when the message that was digested did not start with TPM_GENERATED_VALUE. The ticket is computed by
*/
public class TPMT_TK_HASHCHECK extends TpmStructure
{
    /**
     * This ticket is produced by TPM2_SequenceComplete() when the message that was digested did not start with TPM_GENERATED_VALUE. The ticket is computed by
     * 
     * @param _tag ticket structure tag 
     * @param _hierarchy the hierarchy 
     * @param _digest This shall be the HMAC produced using a proof value of hierarchy.
     */
    public TPMT_TK_HASHCHECK(TPM_ST _tag,TPM_HANDLE _hierarchy,byte[] _digest)
    {
        tag = _tag;
        hierarchy = _hierarchy;
        digest = _digest;
    }
    /**
    * This ticket is produced by TPM2_SequenceComplete() when the message that was digested did not start with TPM_GENERATED_VALUE. The ticket is computed by
    */
    public TPMT_TK_HASHCHECK() {};
    /**
    * ticket structure tag
    */
    public TPM_ST tag;
    /**
    * the hierarchy
    */
    public TPM_HANDLE hierarchy;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short digestSize;
    /**
    * This shall be the HMAC produced using a proof value of hierarchy.
    */
    public byte[] digest;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        tag.toTpm(buf);
        hierarchy.toTpm(buf);
        buf.writeInt((digest!=null)?digest.length:0, 2);
        if(digest!=null)
            buf.write(digest);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        tag = TPM_ST.fromTpm(buf);
        hierarchy = TPM_HANDLE.fromTpm(buf);
        int _digestSize = buf.readInt(2);
        digest = new byte[_digestSize];
        buf.readArrayOfInts(digest, 1, _digestSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMT_TK_HASHCHECK fromTpm (byte[] x) 
    {
        TPMT_TK_HASHCHECK ret = new TPMT_TK_HASHCHECK();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMT_TK_HASHCHECK fromTpm (InByteBuf buf) 
    {
        TPMT_TK_HASHCHECK ret = new TPMT_TK_HASHCHECK();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_TK_HASHCHECK");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ST", "tag", tag);
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
        _p.add(d, "byte", "digest", digest);
    };
    
    
    /**
    * Create a NULL ticket (e.g. used for signing data with non-restricted keys)
    * 
    * @return The null ticket
    */
    public static TPMT_TK_HASHCHECK nullTicket()
    {
        TPMT_TK_HASHCHECK t = new TPMT_TK_HASHCHECK();
        t.tag = TPM_ST.HASHCHECK;
        t.hierarchy = TPM_HANDLE.from(TPM_RH.OWNER);
        return t;
    }
    
    
    
};

//<<<

