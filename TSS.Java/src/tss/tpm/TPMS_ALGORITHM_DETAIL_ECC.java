package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used to report on the curve parameters of an ECC curve. It is
 *  returned by TPM2_ECC_Parameters().
 */
public class TPMS_ALGORITHM_DETAIL_ECC extends TpmStructure
{
    /** Identifier for the curve  */
    public TPM_ECC_CURVE curveID;
    
    /** Size in bits of the key  */
    public int keySize;
    
    /** Scheme selector  */
    public TPM_ALG_ID kdfScheme() { return kdf != null ? kdf.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** If not TPM_ALG_NULL, the required KDF and hash algorithm used in secret sharing operations
     *  One of: TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A, TPMS_KDF_SCHEME_KDF2,
     *  TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH, TPMS_NULL_KDF_SCHEME.
     */
    public TPMU_KDF_SCHEME kdf;
    
    /** Scheme selector  */
    public TPM_ALG_ID signScheme() { return sign != null ? sign.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** If not TPM_ALG_NULL, this is the mandatory signature scheme that is required to be
     *  used with this curve.
     *  One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *  TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *  TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *  TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
     */
    public TPMU_ASYM_SCHEME sign;
    
    /** Fp (the modulus)  */
    public byte[] p;
    
    /** Coefficient of the linear term in the curve equation  */
    public byte[] a;
    
    /** Constant term for curve equation  */
    public byte[] b;
    
    /** X coordinate of base point G  */
    public byte[] gX;
    
    /** Y coordinate of base point G  */
    public byte[] gY;
    
    /** Order of G  */
    public byte[] n;
    
    /** Cofactor (a size of zero indicates a cofactor of 1)  */
    public byte[] h;
    
    public TPMS_ALGORITHM_DETAIL_ECC() {}
    
    /** @param _curveID Identifier for the curve
     *  @param _keySize Size in bits of the key
     *  @param _kdf If not TPM_ALG_NULL, the required KDF and hash algorithm used in secret
     *         sharing operations
     *         One of: TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A, TPMS_KDF_SCHEME_KDF2,
     *         TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH, TPMS_NULL_KDF_SCHEME.
     *  @param _sign If not TPM_ALG_NULL, this is the mandatory signature scheme that is required
     *         to be used with this curve.
     *         One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *         TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *         TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
     *  @param _p Fp (the modulus)
     *  @param _a Coefficient of the linear term in the curve equation
     *  @param _b Constant term for curve equation
     *  @param _gX X coordinate of base point G
     *  @param _gY Y coordinate of base point G
     *  @param _n Order of G
     *  @param _h Cofactor (a size of zero indicates a cofactor of 1)
     */
    public TPMS_ALGORITHM_DETAIL_ECC(TPM_ECC_CURVE _curveID, int _keySize, TPMU_KDF_SCHEME _kdf, TPMU_ASYM_SCHEME _sign, byte[] _p, byte[] _a, byte[] _b, byte[] _gX, byte[] _gY, byte[] _n, byte[] _h)
    {
        curveID = _curveID;
        keySize = _keySize;
        kdf = _kdf;
        sign = _sign;
        p = _p;
        a = _a;
        b = _b;
        gX = _gX;
        gY = _gY;
        n = _n;
        h = _h;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        curveID.toTpm(buf);
        buf.writeShort(keySize);
        buf.writeShort(kdf.GetUnionSelector());
        kdf.toTpm(buf);
        buf.writeShort(sign.GetUnionSelector());
        sign.toTpm(buf);
        buf.writeSizedByteBuf(p);
        buf.writeSizedByteBuf(a);
        buf.writeSizedByteBuf(b);
        buf.writeSizedByteBuf(gX);
        buf.writeSizedByteBuf(gY);
        buf.writeSizedByteBuf(n);
        buf.writeSizedByteBuf(h);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        curveID = TPM_ECC_CURVE.fromTpm(buf);
        keySize = buf.readShort();
        TPM_ALG_ID kdfScheme = TPM_ALG_ID.fromTpm(buf);
        kdf = UnionFactory.create("TPMU_KDF_SCHEME", kdfScheme);
        kdf.initFromTpm(buf);
        TPM_ALG_ID signScheme = TPM_ALG_ID.fromTpm(buf);
        sign = UnionFactory.create("TPMU_ASYM_SCHEME", signScheme);
        sign.initFromTpm(buf);
        p = buf.readSizedByteBuf();
        a = buf.readSizedByteBuf();
        b = buf.readSizedByteBuf();
        gX = buf.readSizedByteBuf();
        gY = buf.readSizedByteBuf();
        n = buf.readSizedByteBuf();
        h = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_ALGORITHM_DETAIL_ECC fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_ALGORITHM_DETAIL_ECC.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_ALGORITHM_DETAIL_ECC fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_ALGORITHM_DETAIL_ECC fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_ALGORITHM_DETAIL_ECC.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ALGORITHM_DETAIL_ECC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ECC_CURVE", "curveID", curveID);
        _p.add(d, "int", "keySize", keySize);
        _p.add(d, "TPMU_KDF_SCHEME", "kdf", kdf);
        _p.add(d, "TPMU_ASYM_SCHEME", "sign", sign);
        _p.add(d, "byte", "p", p);
        _p.add(d, "byte", "a", a);
        _p.add(d, "byte", "b", b);
        _p.add(d, "byte", "gX", gX);
        _p.add(d, "byte", "gY", gY);
        _p.add(d, "byte", "n", n);
        _p.add(d, "byte", "h", h);
    }
}

//<<<
