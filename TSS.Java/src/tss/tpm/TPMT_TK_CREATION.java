package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This ticket is produced by TPM2_Create() or TPM2_CreatePrimary(). It is used to bind the
 *  creation data to the object to which it applies. The ticket is computed by
 */
public class TPMT_TK_CREATION extends TpmStructure
{
    /** the hierarchy containing name */
    public TPM_HANDLE hierarchy;
    
    /** This shall be the HMAC produced using a proof value of hierarchy. */
    public byte[] digest;
    
    public TPMT_TK_CREATION() { hierarchy = new TPM_HANDLE(); }
    
    /**
     *  @param _hierarchy the hierarchy containing name
     *  @param _digest This shall be the HMAC produced using a proof value of hierarchy.
     */
    public TPMT_TK_CREATION(TPM_HANDLE _hierarchy, byte[] _digest)
    {
        hierarchy = _hierarchy;
        digest = _digest;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        TPM_ST.CREATION.toTpm(buf);
        hierarchy.toTpm(buf);
        buf.writeSizedByteBuf(digest);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _tag = buf.readShort() & 0xFFFF;
        assert(_tag == TPM_ST.CREATION.toInt());
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

    public static TPMT_TK_CREATION fromTpm (byte[] x) 
    {
        TPMT_TK_CREATION ret = new TPMT_TK_CREATION();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMT_TK_CREATION fromTpm (InByteBuf buf) 
    {
        TPMT_TK_CREATION ret = new TPMT_TK_CREATION();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_TK_CREATION");
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
