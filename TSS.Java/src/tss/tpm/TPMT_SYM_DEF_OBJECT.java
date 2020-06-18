package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used when different symmetric block cipher (not XOR) algorithms may
 *  be selected. If the Object can be an ordinary parent (not a derivation parent), this
 *  must be the first field in the Object's parameter (see 12.2.3.7) field.
 */
public class TPMT_SYM_DEF_OBJECT extends TpmStructure
{
    /** symmetric algorithm  */
    public TPM_ALG_ID algorithm;
    
    /** key size in bits  */
    public short keyBits;
    
    /** encryption mode  */
    public TPM_ALG_ID mode;
    
    public TPMT_SYM_DEF_OBJECT()
    {
        algorithm = TPM_ALG_ID.NULL;
        mode = TPM_ALG_ID.NULL;
    }
    
    /** @param _algorithm symmetric algorithm
     *  @param _keyBits key size in bits
     *  @param _mode encryption mode
     */
    public TPMT_SYM_DEF_OBJECT(TPM_ALG_ID _algorithm, int _keyBits, TPM_ALG_ID _mode)
    {
        algorithm = _algorithm;
        keyBits = (short)_keyBits;
        mode = _mode;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        algorithm.toTpm(buf);
        if (algorithm == TPM_ALG_ID.NULL) return;
        buf.writeShort(keyBits);
        mode.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        algorithm = TPM_ALG_ID.fromTpm(buf);
        if (algorithm == TPM_ALG_ID.NULL) return;
        keyBits = buf.readShort();
        mode = TPM_ALG_ID.fromTpm(buf);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMT_SYM_DEF_OBJECT fromBytes (byte[] byteBuf) 
    {
        TPMT_SYM_DEF_OBJECT ret = new TPMT_SYM_DEF_OBJECT();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_SYM_DEF_OBJECT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPMT_SYM_DEF_OBJECT fromTpm (InByteBuf buf) 
    {
        TPMT_SYM_DEF_OBJECT ret = new TPMT_SYM_DEF_OBJECT();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SYM_DEF_OBJECT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "algorithm", algorithm);
        _p.add(d, "short", "keyBits", keyBits);
        _p.add(d, "TPM_ALG_ID", "mode", mode);
    }
    
    /** @deprecated Use default constructor instead */
    @Deprecated
    public static TPMT_SYM_DEF_OBJECT nullObject()
    {
     	return new TPMT_SYM_DEF_OBJECT(
    								TPM_ALG_ID.NULL, 
    								(short) 0, 
    								TPM_ALG_ID.NULL
    								);
    }
    
}

//<<<
