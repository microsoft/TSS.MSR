package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe [de]G). It uses the private ephemeral key and a loaded public key (QS) to compute the shared secret value (P [hde]QS).
*/
public class ECDH_KeyGenResponse extends TpmStructure
{
    /**
     * This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe [de]G). It uses the private ephemeral key and a loaded public key (QS) to compute the shared secret value (P [hde]QS).
     * 
     * @param _zPoint results of P h[de]Qs 
     * @param _pubPoint generated ephemeral public point (Qe)
     */
    public ECDH_KeyGenResponse(TPMS_ECC_POINT _zPoint,TPMS_ECC_POINT _pubPoint)
    {
        zPoint = _zPoint;
        pubPoint = _pubPoint;
    }
    /**
    * This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe [de]G). It uses the private ephemeral key and a loaded public key (QS) to compute the shared secret value (P [hde]QS).
    */
    public ECDH_KeyGenResponse() {};
    /**
    * size of the remainder of this structure
    */
    // private short zPointSize;
    /**
    * results of P h[de]Qs
    */
    public TPMS_ECC_POINT zPoint;
    /**
    * size of the remainder of this structure
    */
    // private short pubPointSize;
    /**
    * generated ephemeral public point (Qe)
    */
    public TPMS_ECC_POINT pubPoint;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((zPoint!=null)?zPoint.toTpm().length:0, 2);
        if(zPoint!=null)
            zPoint.toTpm(buf);
        buf.writeInt((pubPoint!=null)?pubPoint.toTpm().length:0, 2);
        if(pubPoint!=null)
            pubPoint.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _zPointSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _zPointSize));
        zPoint = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        int _pubPointSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _pubPointSize));
        pubPoint = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static ECDH_KeyGenResponse fromTpm (byte[] x) 
    {
        ECDH_KeyGenResponse ret = new ECDH_KeyGenResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ECDH_KeyGenResponse fromTpm (InByteBuf buf) 
    {
        ECDH_KeyGenResponse ret = new ECDH_KeyGenResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECDH_KeyGen_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "zPoint", zPoint);
        _p.add(d, "TPMS_ECC_POINT", "pubPoint", pubPoint);
    };
    
    
};

//<<<

