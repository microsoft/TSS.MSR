package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the format used for each of the authorizations in the session area of a command. */
public class TPMS_AUTH_COMMAND extends TpmStructure
{
    /** the session handle */
    public TPM_HANDLE sessionHandle;
    
    /** the session nonce, may be the Empty Buffer */
    public byte[] nonce;
    
    /** the session attributes */
    public TPMA_SESSION sessionAttributes;
    
    /** either an HMAC, a password, or an EmptyAuth */
    public byte[] hmac;
    
    public TPMS_AUTH_COMMAND() { sessionHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _sessionHandle the session handle
     *  @param _nonce the session nonce, may be the Empty Buffer
     *  @param _sessionAttributes the session attributes
     *  @param _hmac either an HMAC, a password, or an EmptyAuth
     */
    public TPMS_AUTH_COMMAND(TPM_HANDLE _sessionHandle, byte[] _nonce, TPMA_SESSION _sessionAttributes, byte[] _hmac)
    {
        sessionHandle = _sessionHandle;
        nonce = _nonce;
        sessionAttributes = _sessionAttributes;
        hmac = _hmac;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        sessionHandle.toTpm(buf);
        buf.writeSizedByteBuf(nonce);
        sessionAttributes.toTpm(buf);
        buf.writeSizedByteBuf(hmac);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        sessionHandle = TPM_HANDLE.fromTpm(buf);
        int _nonceSize = buf.readShort() & 0xFFFF;
        nonce = new byte[_nonceSize];
        buf.readArrayOfInts(nonce, 1, _nonceSize);
        int _sessionAttributes = buf.readByte();
        sessionAttributes = TPMA_SESSION.fromInt(_sessionAttributes);
        int _hmacSize = buf.readShort() & 0xFFFF;
        hmac = new byte[_hmacSize];
        buf.readArrayOfInts(hmac, 1, _hmacSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPMS_AUTH_COMMAND fromTpm (byte[] x) 
    {
        TPMS_AUTH_COMMAND ret = new TPMS_AUTH_COMMAND();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_AUTH_COMMAND fromTpm (InByteBuf buf) 
    {
        TPMS_AUTH_COMMAND ret = new TPMS_AUTH_COMMAND();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_AUTH_COMMAND");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "sessionHandle", sessionHandle);
        _p.add(d, "byte", "nonce", nonce);
        _p.add(d, "TPMA_SESSION", "sessionAttributes", sessionAttributes);
        _p.add(d, "byte", "hmac", hmac);
    }
}

//<<<

