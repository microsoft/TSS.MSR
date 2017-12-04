package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure holds two ECC coordinates that, together, make up an ECC point.
*/
public class TPMS_ECC_POINT extends TpmStructure implements TPMU_PUBLIC_ID 
{
    /**
     * This structure holds two ECC coordinates that, together, make up an ECC point.
     * 
     * @param _x X coordinate 
     * @param _y Y coordinate
     */
    public TPMS_ECC_POINT(byte[] _x,byte[] _y)
    {
        x = _x;
        y = _y;
    }
    /**
    * This structure holds two ECC coordinates that, together, make up an ECC point.
    */
    public TPMS_ECC_POINT() {};
    /**
    * size of buffer
    */
    // private short xSize;
    /**
    * X coordinate
    */
    public byte[] x;
    /**
    * size of buffer
    */
    // private short ySize;
    /**
    * Y coordinate
    */
    public byte[] y;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((x!=null)?x.length:0, 2);
        if(x!=null)
            buf.write(x);
        buf.writeInt((y!=null)?y.length:0, 2);
        if(y!=null)
            buf.write(y);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _xSize = buf.readInt(2);
        x = new byte[_xSize];
        buf.readArrayOfInts(x, 1, _xSize);
        int _ySize = buf.readInt(2);
        y = new byte[_ySize];
        buf.readArrayOfInts(y, 1, _ySize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_ECC_POINT fromTpm (byte[] x) 
    {
        TPMS_ECC_POINT ret = new TPMS_ECC_POINT();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_ECC_POINT fromTpm (InByteBuf buf) 
    {
        TPMS_ECC_POINT ret = new TPMS_ECC_POINT();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ECC_POINT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "x", x);
        _p.add(d, "byte", "y", y);
    };
    
    
};

//<<<

