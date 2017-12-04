package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
*/
public class SequenceCompleteResponse extends TpmStructure
{
    /**
     * This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
     * 
     * @param _result the returned HMAC or digest in a sized buffer 
     * @param _validation ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE This is a NULL Ticket when the sequence is HMAC.
     */
    public SequenceCompleteResponse(byte[] _result,TPMT_TK_HASHCHECK _validation)
    {
        result = _result;
        validation = _validation;
    }
    /**
    * This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
    */
    public SequenceCompleteResponse() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short resultSize;
    /**
    * the returned HMAC or digest in a sized buffer
    */
    public byte[] result;
    /**
    * ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE This is a NULL Ticket when the sequence is HMAC.
    */
    public TPMT_TK_HASHCHECK validation;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((result!=null)?result.length:0, 2);
        if(result!=null)
            buf.write(result);
        validation.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _resultSize = buf.readInt(2);
        result = new byte[_resultSize];
        buf.readArrayOfInts(result, 1, _resultSize);
        validation = TPMT_TK_HASHCHECK.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static SequenceCompleteResponse fromTpm (byte[] x) 
    {
        SequenceCompleteResponse ret = new SequenceCompleteResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static SequenceCompleteResponse fromTpm (InByteBuf buf) 
    {
        SequenceCompleteResponse ret = new SequenceCompleteResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SequenceComplete_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "result", result);
        _p.add(d, "TPMT_TK_HASHCHECK", "validation", validation);
    };
    
    
};

//<<<

