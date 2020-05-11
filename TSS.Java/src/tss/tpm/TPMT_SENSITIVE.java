package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  authValue shall not be larger than the size of the digest produced by the nameAlg of the
 *  object. seedValue shall be the size of the digest produced by the nameAlg of the object.
 */
public class TPMT_SENSITIVE extends TpmStructure
{
    public TPM_ALG_ID sensitiveType() { return sensitive.GetUnionSelector(); }
    
    /**
     *  user authorization data
     *  The authValue may be a zero-length string.
     */
    public byte[] authValue;
    
    /**
     *  for a parent object, the optional protection seed; for other
     *  objects, the obfuscation value
     */
    public byte[] seedValue;
    
    /** the type-specific private data */
    public TPMU_SENSITIVE_COMPOSITE sensitive;
    
    public TPMT_SENSITIVE() {}
    
    /**
     *  @param _authValue user authorization data
     *         The authValue may be a zero-length string.
     *  @param _seedValue for a parent object, the optional protection seed; for other
     *         objects, the obfuscation value
     *  @param _sensitive the type-specific private data
     *         (One of [TPM2B_PRIVATE_KEY_RSA, TPM2B_ECC_PARAMETER, TPM2B_SENSITIVE_DATA, TPM2B_SYM_KEY,
     *         TPM2B_PRIVATE_VENDOR_SPECIFIC])
     */
    public TPMT_SENSITIVE(byte[] _authValue, byte[] _seedValue, TPMU_SENSITIVE_COMPOSITE _sensitive)
    {
        authValue = _authValue;
        seedValue = _seedValue;
        sensitive = _sensitive;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        if (sensitive == null) return;
        sensitive.GetUnionSelector().toTpm(buf);
        buf.writeSizedByteBuf(authValue);
        buf.writeSizedByteBuf(seedValue);
        ((TpmMarshaller)sensitive).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _sensitiveType = buf.readShort() & 0xFFFF;
        int _authValueSize = buf.readShort() & 0xFFFF;
        authValue = new byte[_authValueSize];
        buf.readArrayOfInts(authValue, 1, _authValueSize);
        int _seedValueSize = buf.readShort() & 0xFFFF;
        seedValue = new byte[_seedValueSize];
        buf.readArrayOfInts(seedValue, 1, _seedValueSize);
        sensitive = UnionFactory.create("TPMU_SENSITIVE_COMPOSITE", new TPM_ALG_ID(_sensitiveType));
        sensitive.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPMT_SENSITIVE fromTpm (byte[] x) 
    {
        TPMT_SENSITIVE ret = new TPMT_SENSITIVE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMT_SENSITIVE fromTpm (InByteBuf buf) 
    {
        TPMT_SENSITIVE ret = new TPMT_SENSITIVE();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SENSITIVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "authValue", authValue);
        _p.add(d, "byte", "seedValue", seedValue);
        _p.add(d, "TPMU_SENSITIVE_COMPOSITE", "sensitive", sensitive);
    }
}

//<<<
