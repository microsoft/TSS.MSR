package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs a hash operation on a data buffer and returns the results. */
public class TPM2_Hash_REQUEST extends TpmStructure
{
    /** data to be hashed */
    public byte[] data;
    
    /** algorithm for the hash being computed shall not be TPM_ALG_NULL */
    public TPM_ALG_ID hashAlg;
    
    /** hierarchy to use for the ticket (TPM_RH_NULL allowed) */
    public TPM_HANDLE hierarchy;
    
    public TPM2_Hash_REQUEST()
    {
        hashAlg = TPM_ALG_ID.NULL;
        hierarchy = new TPM_HANDLE();
    }

    /**
     *  @param _data data to be hashed
     *  @param _hashAlg algorithm for the hash being computed shall not be TPM_ALG_NULL
     *  @param _hierarchy hierarchy to use for the ticket (TPM_RH_NULL allowed)
     */
    public TPM2_Hash_REQUEST(byte[] _data, TPM_ALG_ID _hashAlg, TPM_HANDLE _hierarchy)
    {
        data = _data;
        hashAlg = _hashAlg;
        hierarchy = _hierarchy;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(data);
        hashAlg.toTpm(buf);
        hierarchy.toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _dataSize = buf.readShort() & 0xFFFF;
        data = new byte[_dataSize];
        buf.readArrayOfInts(data, 1, _dataSize);
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        hierarchy = TPM_HANDLE.fromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_Hash_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Hash_REQUEST ret = new TPM2_Hash_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_Hash_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Hash_REQUEST ret = new TPM2_Hash_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Hash_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "data", data);
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
    }
}

//<<<
