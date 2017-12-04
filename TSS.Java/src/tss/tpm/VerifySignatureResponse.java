package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command uses loaded keys to validate a signature on a message with the message digest passed to the TPM.
*/
public class VerifySignatureResponse extends TpmStructure
{
    /**
     * This command uses loaded keys to validate a signature on a message with the message digest passed to the TPM.
     * 
     * @param _validation -
     */
    public VerifySignatureResponse(TPMT_TK_VERIFIED _validation)
    {
        validation = _validation;
    }
    /**
    * This command uses loaded keys to validate a signature on a message with the message digest passed to the TPM.
    */
    public VerifySignatureResponse() {};
    public TPMT_TK_VERIFIED validation;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        validation.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        validation = TPMT_TK_VERIFIED.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static VerifySignatureResponse fromTpm (byte[] x) 
    {
        VerifySignatureResponse ret = new VerifySignatureResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static VerifySignatureResponse fromTpm (InByteBuf buf) 
    {
        VerifySignatureResponse ret = new VerifySignatureResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_VerifySignature_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_TK_VERIFIED", "validation", validation);
    };
    
    
};

//<<<

