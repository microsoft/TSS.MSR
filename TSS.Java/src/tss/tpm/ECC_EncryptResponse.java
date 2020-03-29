package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command performs ECC encryption as described in Part 1, Annex D.
*/
public class ECC_EncryptResponse extends TpmStructure
{
    /**
     * @param _C1 the public ephemeral key used for ECDH 
     * @param _C2 the data block produced by the XOR process 
     * @param _C3 the integrity value
     */
    public ECC_EncryptResponse(TPMS_ECC_POINT _C1,byte[] _C2,byte[] _C3)
    {
        C1 = _C1;
        C2 = _C2;
        C3 = _C3;
    }
    /**
    * This command performs ECC encryption as described in Part 1, Annex D.
    */
    public ECC_EncryptResponse() {};
    /**
    * size of the remainder of this structure
    */
    // private short C1Size;
    /**
    * the public ephemeral key used for ECDH
    */
    public TPMS_ECC_POINT C1;
    /**
    * size of the buffer
    */
    // private short C2Size;
    /**
    * the data block produced by the XOR process
    */
    public byte[] C2;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short C3Size;
    /**
    * the integrity value
    */
    public byte[] C3;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((C1!=null)?C1.toTpm().length:0, 2);
        if(C1!=null)
            C1.toTpm(buf);
        buf.writeInt((C2!=null)?C2.length:0, 2);
        if(C2!=null)
            buf.write(C2);
        buf.writeInt((C3!=null)?C3.length:0, 2);
        if(C3!=null)
            buf.write(C3);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _C1Size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _C1Size));
        C1 = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        int _C2Size = buf.readInt(2);
        C2 = new byte[_C2Size];
        buf.readArrayOfInts(C2, 1, _C2Size);
        int _C3Size = buf.readInt(2);
        C3 = new byte[_C3Size];
        buf.readArrayOfInts(C3, 1, _C3Size);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static ECC_EncryptResponse fromTpm (byte[] x) 
    {
        ECC_EncryptResponse ret = new ECC_EncryptResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ECC_EncryptResponse fromTpm (InByteBuf buf) 
    {
        ECC_EncryptResponse ret = new ECC_EncryptResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECC_Encrypt_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "C1", C1);
        _p.add(d, "byte", "C2", C2);
        _p.add(d, "byte", "C3", C3);
    };
    
    
};

//<<<

