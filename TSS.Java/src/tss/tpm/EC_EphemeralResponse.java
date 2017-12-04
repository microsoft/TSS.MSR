package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.
*/
public class EC_EphemeralResponse extends TpmStructure
{
    /**
     * TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.
     * 
     * @param _Q ephemeral public key Q [r]G 
     * @param _counter least-significant 16 bits of commitCount
     */
    public EC_EphemeralResponse(TPMS_ECC_POINT _Q,int _counter)
    {
        Q = _Q;
        counter = (short)_counter;
    }
    /**
    * TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.
    */
    public EC_EphemeralResponse() {};
    /**
    * size of the remainder of this structure
    */
    // private short QSize;
    /**
    * ephemeral public key Q [r]G
    */
    public TPMS_ECC_POINT Q;
    /**
    * least-significant 16 bits of commitCount
    */
    public short counter;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((Q!=null)?Q.toTpm().length:0, 2);
        if(Q!=null)
            Q.toTpm(buf);
        buf.write(counter);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _QSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _QSize));
        Q = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        counter = (short) buf.readInt(2);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static EC_EphemeralResponse fromTpm (byte[] x) 
    {
        EC_EphemeralResponse ret = new EC_EphemeralResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static EC_EphemeralResponse fromTpm (InByteBuf buf) 
    {
        EC_EphemeralResponse ret = new EC_EphemeralResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EC_Ephemeral_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "Q", Q);
        _p.add(d, "ushort", "counter", counter);
    };
    
    
};

//<<<

