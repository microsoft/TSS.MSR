package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command uses the TPM to recover the Z value from a public point (QB) and a private key (ds). It will perform the multiplication of the provided inPoint (QB) with the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ) [hds]QB; where h is the cofactor of the curve).
*/
public class ECDH_ZGenResponse extends TpmStructure
{
    /**
     * This command uses the TPM to recover the Z value from a public point (QB) and a private key (ds). It will perform the multiplication of the provided inPoint (QB) with the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ) [hds]QB; where h is the cofactor of the curve).
     * 
     * @param _outPoint X and Y coordinates of the product of the multiplication Z = (xZ , yZ) [hdS]QB
     */
    public ECDH_ZGenResponse(TPMS_ECC_POINT _outPoint)
    {
        outPoint = _outPoint;
    }
    /**
    * This command uses the TPM to recover the Z value from a public point (QB) and a private key (ds). It will perform the multiplication of the provided inPoint (QB) with the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ) [hds]QB; where h is the cofactor of the curve).
    */
    public ECDH_ZGenResponse() {};
    /**
    * size of the remainder of this structure
    */
    // private short outPointSize;
    /**
    * X and Y coordinates of the product of the multiplication Z = (xZ , yZ) [hdS]QB
    */
    public TPMS_ECC_POINT outPoint;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((outPoint!=null)?outPoint.toTpm().length:0, 2);
        if(outPoint!=null)
            outPoint.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outPointSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _outPointSize));
        outPoint = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static ECDH_ZGenResponse fromTpm (byte[] x) 
    {
        ECDH_ZGenResponse ret = new ECDH_ZGenResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ECDH_ZGenResponse fromTpm (InByteBuf buf) 
    {
        ECDH_ZGenResponse ret = new ECDH_ZGenResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECDH_ZGen_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "outPoint", outPoint);
    };
    
    
};

//<<<

