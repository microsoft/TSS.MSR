package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is defined to allow a point to be a single sized parameter so that it may be encrypted.
*/
public class TPM2B_ECC_POINT extends TpmStructure
{
    /**
     * This structure is defined to allow a point to be a single sized parameter so that it may be encrypted.
     * 
     * @param _point coordinates
     */
    public TPM2B_ECC_POINT(TPMS_ECC_POINT _point)
    {
        point = _point;
    }
    /**
    * This structure is defined to allow a point to be a single sized parameter so that it may be encrypted.
    */
    public TPM2B_ECC_POINT() {};
    /**
    * size of the remainder of this structure
    */
    // private short size;
    /**
    * coordinates
    */
    public TPMS_ECC_POINT point;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((point!=null)?point.toTpm().length:0, 2);
        if(point!=null)
            point.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _size));
        point = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2B_ECC_POINT fromTpm (byte[] x) 
    {
        TPM2B_ECC_POINT ret = new TPM2B_ECC_POINT();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_ECC_POINT fromTpm (InByteBuf buf) 
    {
        TPM2B_ECC_POINT ret = new TPM2B_ECC_POINT();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_ECC_POINT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "point", point);
    };
    
    
};

//<<<

