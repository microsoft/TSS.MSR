package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the attested data for TPM2_GetSessionAuditDigest(). */
public class TPMS_SESSION_AUDIT_INFO extends TpmStructure implements TPMU_ATTEST
{
    /**
     *  current exclusive status of the session
     *  TRUE if all of the commands recorded in the sessionDigest were executed without any
     *  intervening TPM command that did not use this audit session
     */
    public byte exclusiveSession;
    
    /** the current value of the session audit digest */
    public byte[] sessionDigest;
    
    public TPMS_SESSION_AUDIT_INFO() {}
    
    /**
     *  @param _exclusiveSession current exclusive status of the session
     *         TRUE if all of the commands recorded in the sessionDigest were executed without any
     *         intervening TPM command that did not use this audit session
     *  @param _sessionDigest the current value of the session audit digest
     */
    public TPMS_SESSION_AUDIT_INFO(byte _exclusiveSession, byte[] _sessionDigest)
    {
        exclusiveSession = _exclusiveSession;
        sessionDigest = _sessionDigest;
    }

    /** TpmUnion method */
    public TPM_ST GetUnionSelector() { return TPM_ST.ATTEST_SESSION_AUDIT; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeByte(exclusiveSession);
        buf.writeSizedByteBuf(sessionDigest);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        exclusiveSession = buf.readByte();
        int _sessionDigestSize = buf.readShort() & 0xFFFF;
        sessionDigest = new byte[_sessionDigestSize];
        buf.readArrayOfInts(sessionDigest, 1, _sessionDigestSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPMS_SESSION_AUDIT_INFO fromTpm (byte[] x) 
    {
        TPMS_SESSION_AUDIT_INFO ret = new TPMS_SESSION_AUDIT_INFO();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_SESSION_AUDIT_INFO fromTpm (InByteBuf buf) 
    {
        TPMS_SESSION_AUDIT_INFO ret = new TPMS_SESSION_AUDIT_INFO();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SESSION_AUDIT_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "exclusiveSession", exclusiveSession);
        _p.add(d, "byte", "sessionDigest", sessionDigest);
    }
}

//<<<
