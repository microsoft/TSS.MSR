package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 187 Definition of {ECC} TPMS_SIGNATURE_ECC Structure */
public class TPMS_SIGNATURE_ECC extends TpmStructure implements TPMU_SIGNATURE
{
    /**
     *  the hash algorithm used in the signature process
     *  TPM_ALG_NULL is not allowed.
     */
    public TPM_ALG_ID hash;
    
    public byte[] signatureR;
    
    public byte[] signatureS;
    
    public TPMS_SIGNATURE_ECC() { hash = TPM_ALG_ID.NULL; }
    
    /**
     *  @param _hash the hash algorithm used in the signature process
     *         TPM_ALG_NULL is not allowed.
     *  @param _signatureR TBD
     *  @param _signatureS TBD
     */
    public TPMS_SIGNATURE_ECC(TPM_ALG_ID _hash, byte[] _signatureR, byte[] _signatureS)
    {
        hash = _hash;
        signatureR = _signatureR;
        signatureS = _signatureS;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        hash.toTpm(buf);
        buf.writeSizedByteBuf(signatureR);
        buf.writeSizedByteBuf(signatureS);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        hash = TPM_ALG_ID.fromTpm(buf);
        int _signatureRSize = buf.readShort() & 0xFFFF;
        signatureR = new byte[_signatureRSize];
        buf.readArrayOfInts(signatureR, 1, _signatureRSize);
        int _signatureSSize = buf.readShort() & 0xFFFF;
        signatureS = new byte[_signatureSSize];
        buf.readArrayOfInts(signatureS, 1, _signatureSSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPMS_SIGNATURE_ECC fromTpm (byte[] x) 
    {
        TPMS_SIGNATURE_ECC ret = new TPMS_SIGNATURE_ECC();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_SIGNATURE_ECC fromTpm (InByteBuf buf) 
    {
        TPMS_SIGNATURE_ECC ret = new TPMS_SIGNATURE_ECC();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIGNATURE_ECC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hash", hash);
        _p.add(d, "byte", "signatureR", signatureR);
        _p.add(d, "byte", "signatureS", signatureS);
    }
}

//<<<

