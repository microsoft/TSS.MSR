package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used to report on the curve parameters of an ECC curve. It is returned by TPM2_ECC_Parameters().
*/
public class TPMS_ALGORITHM_DETAIL_ECC extends TpmStructure
{
    /**
     * This structure is used to report on the curve parameters of an ECC curve. It is returned by TPM2_ECC_Parameters().
     * 
     * @param _curveID identifier for the curve 
     * @param _keySize Size in bits of the key 
     * @param _kdf if not TPM_ALG_NULL, the required KDF and hash algorithm used in secret sharing operations (One of TPMS_SCHEME_MGF1, TPMS_SCHEME_KDF1_SP800_56A, TPMS_SCHEME_KDF2, TPMS_SCHEME_KDF1_SP800_108, TPMS_NULL_KDF_SCHEME) 
     * @param _sign If not TPM_ALG_NULL, this is the mandatory signature scheme that is required to be used with this curve. (One of TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME) 
     * @param _p Fp (the modulus) 
     * @param _a coefficient of the linear term in the curve equation 
     * @param _b constant term for curve equation 
     * @param _gX x coordinate of base point G 
     * @param _gY y coordinate of base point G 
     * @param _n order of G 
     * @param _h cofactor (a size of zero indicates a cofactor of 1)
     */
    public TPMS_ALGORITHM_DETAIL_ECC(TPM_ECC_CURVE _curveID,int _keySize,TPMU_KDF_SCHEME _kdf,TPMU_ASYM_SCHEME _sign,byte[] _p,byte[] _a,byte[] _b,byte[] _gX,byte[] _gY,byte[] _n,byte[] _h)
    {
        curveID = _curveID;
        keySize = (short)_keySize;
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
    /**
    * This structure is used to report on the curve parameters of an ECC curve. It is returned by TPM2_ECC_Parameters().
    */
    public TPMS_ALGORITHM_DETAIL_ECC() {};
    /**
    * identifier for the curve
    */
    public TPM_ECC_CURVE curveID;
    /**
    * Size in bits of the key
    */
    public short keySize;
    /**
    * scheme selector
    */
    // private TPM_ALG_ID kdfScheme;
    /**
    * if not TPM_ALG_NULL, the required KDF and hash algorithm used in secret sharing operations
    */
    public TPMU_KDF_SCHEME kdf;
    /**
    * scheme selector
    */
    // private TPM_ALG_ID signScheme;
    /**
    * If not TPM_ALG_NULL, this is the mandatory signature scheme that is required to be used with this curve.
    */
    public TPMU_ASYM_SCHEME sign;
    /**
    * size of buffer
    */
    // private short pSize;
    /**
    * Fp (the modulus)
    */
    public byte[] p;
    /**
    * size of buffer
    */
    // private short aSize;
    /**
    * coefficient of the linear term in the curve equation
    */
    public byte[] a;
    /**
    * size of buffer
    */
    // private short bSize;
    /**
    * constant term for curve equation
    */
    public byte[] b;
    /**
    * size of buffer
    */
    // private short gXSize;
    /**
    * x coordinate of base point G
    */
    public byte[] gX;
    /**
    * size of buffer
    */
    // private short gYSize;
    /**
    * y coordinate of base point G
    */
    public byte[] gY;
    /**
    * size of buffer
    */
    // private short nSize;
    /**
    * order of G
    */
    public byte[] n;
    /**
    * size of buffer
    */
    // private short hSize;
    /**
    * cofactor (a size of zero indicates a cofactor of 1)
    */
    public byte[] h;
    public int GetUnionSelector_kdf()
    {
        if(kdf instanceof TPMS_SCHEME_MGF1){return 0x0007; }
        if(kdf instanceof TPMS_SCHEME_KDF1_SP800_56A){return 0x0020; }
        if(kdf instanceof TPMS_SCHEME_KDF2){return 0x0021; }
        if(kdf instanceof TPMS_SCHEME_KDF1_SP800_108){return 0x0022; }
        if(kdf instanceof TPMS_NULL_KDF_SCHEME){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    public int GetUnionSelector_sign()
    {
        if(sign instanceof TPMS_KEY_SCHEME_ECDH){return 0x0019; }
        if(sign instanceof TPMS_KEY_SCHEME_ECMQV){return 0x001D; }
        if(sign instanceof TPMS_SIG_SCHEME_RSASSA){return 0x0014; }
        if(sign instanceof TPMS_SIG_SCHEME_RSAPSS){return 0x0016; }
        if(sign instanceof TPMS_SIG_SCHEME_ECDSA){return 0x0018; }
        if(sign instanceof TPMS_SIG_SCHEME_ECDAA){return 0x001A; }
        if(sign instanceof TPMS_SIG_SCHEME_SM2){return 0x001B; }
        if(sign instanceof TPMS_SIG_SCHEME_ECSCHNORR){return 0x001C; }
        if(sign instanceof TPMS_ENC_SCHEME_RSAES){return 0x0015; }
        if(sign instanceof TPMS_ENC_SCHEME_OAEP){return 0x0017; }
        if(sign instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(sign instanceof TPMS_NULL_ASYM_SCHEME){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        curveID.toTpm(buf);
        buf.write(keySize);
        buf.writeInt(GetUnionSelector_kdf(), 2);
        ((TpmMarshaller)kdf).toTpm(buf);
        buf.writeInt(GetUnionSelector_sign(), 2);
        ((TpmMarshaller)sign).toTpm(buf);
        buf.writeInt((p!=null)?p.length:0, 2);
        if(p!=null)
            buf.write(p);
        buf.writeInt((a!=null)?a.length:0, 2);
        if(a!=null)
            buf.write(a);
        buf.writeInt((b!=null)?b.length:0, 2);
        if(b!=null)
            buf.write(b);
        buf.writeInt((gX!=null)?gX.length:0, 2);
        if(gX!=null)
            buf.write(gX);
        buf.writeInt((gY!=null)?gY.length:0, 2);
        if(gY!=null)
            buf.write(gY);
        buf.writeInt((n!=null)?n.length:0, 2);
        if(n!=null)
            buf.write(n);
        buf.writeInt((h!=null)?h.length:0, 2);
        if(h!=null)
            buf.write(h);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        curveID = TPM_ECC_CURVE.fromTpm(buf);
        keySize = (short) buf.readInt(2);
        int _kdfScheme = buf.readInt(2);
        kdf=null;
        if(_kdfScheme==TPM_ALG_ID.MGF1.toInt()) {kdf = new TPMS_SCHEME_MGF1();}
        else if(_kdfScheme==TPM_ALG_ID.KDF1_SP800_56A.toInt()) {kdf = new TPMS_SCHEME_KDF1_SP800_56A();}
        else if(_kdfScheme==TPM_ALG_ID.KDF2.toInt()) {kdf = new TPMS_SCHEME_KDF2();}
        else if(_kdfScheme==TPM_ALG_ID.KDF1_SP800_108.toInt()) {kdf = new TPMS_SCHEME_KDF1_SP800_108();}
        else if(_kdfScheme==TPM_ALG_ID.NULL.toInt()) {kdf = new TPMS_NULL_KDF_SCHEME();}
        if(kdf==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_kdfScheme).name());
        kdf.initFromTpm(buf);
        int _signScheme = buf.readInt(2);
        sign=null;
        if(_signScheme==TPM_ALG_ID.ECDH.toInt()) {sign = new TPMS_KEY_SCHEME_ECDH();}
        else if(_signScheme==TPM_ALG_ID.ECMQV.toInt()) {sign = new TPMS_KEY_SCHEME_ECMQV();}
        else if(_signScheme==TPM_ALG_ID.RSASSA.toInt()) {sign = new TPMS_SIG_SCHEME_RSASSA();}
        else if(_signScheme==TPM_ALG_ID.RSAPSS.toInt()) {sign = new TPMS_SIG_SCHEME_RSAPSS();}
        else if(_signScheme==TPM_ALG_ID.ECDSA.toInt()) {sign = new TPMS_SIG_SCHEME_ECDSA();}
        else if(_signScheme==TPM_ALG_ID.ECDAA.toInt()) {sign = new TPMS_SIG_SCHEME_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_signScheme==TPM_ALG_ID.SM2.toInt()) {sign = new TPMS_SIG_SCHEME_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_signScheme==TPM_ALG_ID.ECSCHNORR.toInt()) {sign = new TPMS_SIG_SCHEME_ECSCHNORR();}
        else if(_signScheme==TPM_ALG_ID.RSAES.toInt()) {sign = new TPMS_ENC_SCHEME_RSAES();}
        else if(_signScheme==TPM_ALG_ID.OAEP.toInt()) {sign = new TPMS_ENC_SCHEME_OAEP();}
        else if(_signScheme==TPM_ALG_ID.ANY.toInt()) {sign = new TPMS_SCHEME_HASH();}
        else if(_signScheme==TPM_ALG_ID.NULL.toInt()) {sign = new TPMS_NULL_ASYM_SCHEME();}
        if(sign==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_signScheme).name());
        sign.initFromTpm(buf);
        int _pSize = buf.readInt(2);
        p = new byte[_pSize];
        buf.readArrayOfInts(p, 1, _pSize);
        int _aSize = buf.readInt(2);
        a = new byte[_aSize];
        buf.readArrayOfInts(a, 1, _aSize);
        int _bSize = buf.readInt(2);
        b = new byte[_bSize];
        buf.readArrayOfInts(b, 1, _bSize);
        int _gXSize = buf.readInt(2);
        gX = new byte[_gXSize];
        buf.readArrayOfInts(gX, 1, _gXSize);
        int _gYSize = buf.readInt(2);
        gY = new byte[_gYSize];
        buf.readArrayOfInts(gY, 1, _gYSize);
        int _nSize = buf.readInt(2);
        n = new byte[_nSize];
        buf.readArrayOfInts(n, 1, _nSize);
        int _hSize = buf.readInt(2);
        h = new byte[_hSize];
        buf.readArrayOfInts(h, 1, _hSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_ALGORITHM_DETAIL_ECC fromTpm (byte[] x) 
    {
        TPMS_ALGORITHM_DETAIL_ECC ret = new TPMS_ALGORITHM_DETAIL_ECC();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_ALGORITHM_DETAIL_ECC fromTpm (InByteBuf buf) 
    {
        TPMS_ALGORITHM_DETAIL_ECC ret = new TPMS_ALGORITHM_DETAIL_ECC();
        ret.initFromTpm(buf);
        return ret;
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
        _p.add(d, "ushort", "keySize", keySize);
        _p.add(d, "TPMU_KDF_SCHEME", "kdf", kdf);
        _p.add(d, "TPMU_ASYM_SCHEME", "sign", sign);
        _p.add(d, "byte", "p", p);
        _p.add(d, "byte", "a", a);
        _p.add(d, "byte", "b", b);
        _p.add(d, "byte", "gX", gX);
        _p.add(d, "byte", "gY", gY);
        _p.add(d, "byte", "n", n);
        _p.add(d, "byte", "h", h);
    };
    
    
};

//<<<

