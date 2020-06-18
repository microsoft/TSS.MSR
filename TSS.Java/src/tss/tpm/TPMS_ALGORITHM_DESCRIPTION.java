package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is a return value for a TPM2_GetCapability() that reads the installed algorithms.  */
public class TPMS_ALGORITHM_DESCRIPTION extends TpmStructure
{
    /** An algorithm  */
    public TPM_ALG_ID alg;
    
    /** The attributes of the algorithm  */
    public TPMA_ALGORITHM attributes;
    
    public TPMS_ALGORITHM_DESCRIPTION() { alg = TPM_ALG_ID.NULL; }
    
    /** @param _alg An algorithm
     *  @param _attributes The attributes of the algorithm
     */
    public TPMS_ALGORITHM_DESCRIPTION(TPM_ALG_ID _alg, TPMA_ALGORITHM _attributes)
    {
        alg = _alg;
        attributes = _attributes;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        alg.toTpm(buf);
        attributes.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        alg = TPM_ALG_ID.fromTpm(buf);
        int _attributes = buf.readInt();
        attributes = TPMA_ALGORITHM.fromInt(_attributes);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMS_ALGORITHM_DESCRIPTION fromBytes (byte[] byteBuf) 
    {
        TPMS_ALGORITHM_DESCRIPTION ret = new TPMS_ALGORITHM_DESCRIPTION();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_ALGORITHM_DESCRIPTION fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPMS_ALGORITHM_DESCRIPTION fromTpm (InByteBuf buf) 
    {
        TPMS_ALGORITHM_DESCRIPTION ret = new TPMS_ALGORITHM_DESCRIPTION();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ALGORITHM_DESCRIPTION");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "alg", alg);
        _p.add(d, "TPMA_ALGORITHM", "attributes", attributes);
    }
}

//<<<
