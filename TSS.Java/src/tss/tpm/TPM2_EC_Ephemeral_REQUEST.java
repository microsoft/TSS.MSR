package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.
*/
public class TPM2_EC_Ephemeral_REQUEST extends TpmStructure
{
    /**
     * TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.
     * 
     * @param _curveID The curve for the computed ephemeral point
     */
    public TPM2_EC_Ephemeral_REQUEST(TPM_ECC_CURVE _curveID)
    {
        curveID = _curveID;
    }
    /**
    * TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.
    */
    public TPM2_EC_Ephemeral_REQUEST() {};
    /**
    * The curve for the computed ephemeral point
    */
    public TPM_ECC_CURVE curveID;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        curveID.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        curveID = TPM_ECC_CURVE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_EC_Ephemeral_REQUEST fromTpm (byte[] x) 
    {
        TPM2_EC_Ephemeral_REQUEST ret = new TPM2_EC_Ephemeral_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_EC_Ephemeral_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_EC_Ephemeral_REQUEST ret = new TPM2_EC_Ephemeral_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EC_Ephemeral_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ECC_CURVE", "curveID", curveID);
    };
    
    
};

//<<<

