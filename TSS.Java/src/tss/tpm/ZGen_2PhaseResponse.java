package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command supports two-phase key exchange protocols. The command is used in combination with TPM2_EC_Ephemeral(). TPM2_EC_Ephemeral() generates an ephemeral key and returns the public point of that ephemeral key along with a numeric value that allows the TPM to regenerate the associated private key.
*/
public class ZGen_2PhaseResponse extends TpmStructure
{
    /**
     * This command supports two-phase key exchange protocols. The command is used in combination with TPM2_EC_Ephemeral(). TPM2_EC_Ephemeral() generates an ephemeral key and returns the public point of that ephemeral key along with a numeric value that allows the TPM to regenerate the associated private key.
     * 
     * @param _outZ1 X and Y coordinates of the computed value (scheme dependent) 
     * @param _outZ2 X and Y coordinates of the second computed value (scheme dependent)
     */
    public ZGen_2PhaseResponse(TPMS_ECC_POINT _outZ1,TPMS_ECC_POINT _outZ2)
    {
        outZ1 = _outZ1;
        outZ2 = _outZ2;
    }
    /**
    * This command supports two-phase key exchange protocols. The command is used in combination with TPM2_EC_Ephemeral(). TPM2_EC_Ephemeral() generates an ephemeral key and returns the public point of that ephemeral key along with a numeric value that allows the TPM to regenerate the associated private key.
    */
    public ZGen_2PhaseResponse() {};
    /**
    * size of the remainder of this structure
    */
    // private short outZ1Size;
    /**
    * X and Y coordinates of the computed value (scheme dependent)
    */
    public TPMS_ECC_POINT outZ1;
    /**
    * size of the remainder of this structure
    */
    // private short outZ2Size;
    /**
    * X and Y coordinates of the second computed value (scheme dependent)
    */
    public TPMS_ECC_POINT outZ2;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((outZ1!=null)?outZ1.toTpm().length:0, 2);
        if(outZ1!=null)
            outZ1.toTpm(buf);
        buf.writeInt((outZ2!=null)?outZ2.toTpm().length:0, 2);
        if(outZ2!=null)
            outZ2.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outZ1Size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _outZ1Size));
        outZ1 = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        int _outZ2Size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _outZ2Size));
        outZ2 = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static ZGen_2PhaseResponse fromTpm (byte[] x) 
    {
        ZGen_2PhaseResponse ret = new ZGen_2PhaseResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ZGen_2PhaseResponse fromTpm (InByteBuf buf) 
    {
        ZGen_2PhaseResponse ret = new ZGen_2PhaseResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ZGen_2Phase_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "outZ1", outZ1);
        _p.add(d, "TPMS_ECC_POINT", "outZ2", outZ2);
    };
    
    
};

//<<<

