package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This ticket is produced by TPM2_VerifySignature(). This formulation is used for multiple
 *  ticket uses. The ticket provides evidence that the TPM has validated that a digest was
 *  signed by a key with the Name of keyName. The ticket is computed by
 */
public class TPMT_TK_VERIFIED extends TpmStructure
{
    /** the hierarchy containing keyName */
    public TPM_HANDLE hierarchy;
    
    /** This shall be the HMAC produced using a proof value of hierarchy. */
    public byte[] digest;
    
    public TPMT_TK_VERIFIED() { hierarchy = new TPM_HANDLE(); }
    
    /**
     *  @param _hierarchy the hierarchy containing keyName
     *  @param _digest This shall be the HMAC produced using a proof value of hierarchy.
     */
    public TPMT_TK_VERIFIED(TPM_HANDLE _hierarchy, byte[] _digest)
    {
        hierarchy = _hierarchy;
        digest = _digest;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        TPM_ST.VERIFIED.toTpm(buf);
        hierarchy.toTpm(buf);
        buf.writeSizedByteBuf(digest);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _tag = buf.readShort() & 0xFFFF;
        assert(_tag == TPM_ST.VERIFIED.toInt());
        hierarchy = TPM_HANDLE.fromTpm(buf);
        int _digestSize = buf.readShort() & 0xFFFF;
        digest = new byte[_digestSize];
        buf.readArrayOfInts(digest, 1, _digestSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPMT_TK_VERIFIED fromTpm (byte[] x) 
    {
        TPMT_TK_VERIFIED ret = new TPMT_TK_VERIFIED();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMT_TK_VERIFIED fromTpm (InByteBuf buf) 
    {
        TPMT_TK_VERIFIED ret = new TPMT_TK_VERIFIED();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_TK_VERIFIED");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
        _p.add(d, "byte", "digest", digest);
    }
}

//<<<
