package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command uses the TPM to recover the Z value from a public point (QB) and a private key (ds). It will perform the multiplication of the provided inPoint (QB) with the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ) [hds]QB; where h is the cofactor of the curve).
*/
public class TPM2_ECDH_ZGen_REQUEST extends TpmStructure
{
    /**
     * This command uses the TPM to recover the Z value from a public point (QB) and a private key (ds). It will perform the multiplication of the provided inPoint (QB) with the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ) [hds]QB; where h is the cofactor of the curve).
     * 
     * @param _keyHandle handle of a loaded ECC key Auth Index: 1 Auth Role: USER 
     * @param _inPoint a public key
     */
    public TPM2_ECDH_ZGen_REQUEST(TPM_HANDLE _keyHandle,TPMS_ECC_POINT _inPoint)
    {
        keyHandle = _keyHandle;
        inPoint = _inPoint;
    }
    /**
    * This command uses the TPM to recover the Z value from a public point (QB) and a private key (ds). It will perform the multiplication of the provided inPoint (QB) with the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ) [hds]QB; where h is the cofactor of the curve).
    */
    public TPM2_ECDH_ZGen_REQUEST() {};
    /**
    * handle of a loaded ECC key Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE keyHandle;
    /**
    * size of the remainder of this structure
    */
    // private short inPointSize;
    /**
    * a public key
    */
    public TPMS_ECC_POINT inPoint;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        keyHandle.toTpm(buf);
        buf.writeInt((inPoint!=null)?inPoint.toTpm().length:0, 2);
        if(inPoint!=null)
            inPoint.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        keyHandle = TPM_HANDLE.fromTpm(buf);
        int _inPointSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inPointSize));
        inPoint = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ECDH_ZGen_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ECDH_ZGen_REQUEST ret = new TPM2_ECDH_ZGen_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ECDH_ZGen_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ECDH_ZGen_REQUEST ret = new TPM2_ECDH_ZGen_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECDH_ZGen_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "TPMS_ECC_POINT", "inPoint", inPoint);
    };
    
    
};

//<<<

