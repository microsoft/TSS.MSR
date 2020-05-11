package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command performs an HMAC or a block cipher MAC on the supplied data
 *  using the indicated algorithm.
 */
public class TPM2_MAC_REQUEST extends TpmStructure
{
    /**
     *  handle for the symmetric signing key providing the MAC key
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE handle;
    
    /** MAC data */
    public byte[] buffer;
    
    /** algorithm to use for MAC */
    public TPM_ALG_ID inScheme;
    
    public TPM2_MAC_REQUEST()
    {
        handle = new TPM_HANDLE();
        inScheme = TPM_ALG_ID.NULL;
    }

    /**
     *  @param _handle handle for the symmetric signing key providing the MAC key
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _buffer MAC data
     *  @param _inScheme algorithm to use for MAC
     */
    public TPM2_MAC_REQUEST(TPM_HANDLE _handle, byte[] _buffer, TPM_ALG_ID _inScheme)
    {
        handle = _handle;
        buffer = _buffer;
        inScheme = _inScheme;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        buf.writeSizedByteBuf(buffer);
        inScheme.toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        int _bufferSize = buf.readShort() & 0xFFFF;
        buffer = new byte[_bufferSize];
        buf.readArrayOfInts(buffer, 1, _bufferSize);
        inScheme = TPM_ALG_ID.fromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_MAC_REQUEST fromTpm (byte[] x) 
    {
        TPM2_MAC_REQUEST ret = new TPM2_MAC_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_MAC_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_MAC_REQUEST ret = new TPM2_MAC_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_MAC_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte", "buffer", buffer);
        _p.add(d, "TPM_ALG_ID", "inScheme", inScheme);
    }
}

//<<<
