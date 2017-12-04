package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* TPMS_AC_OUTPUT is used to return information about an AC. The tag structure parameter indicates the type of the data value.
*/
public class TPMS_AC_OUTPUT extends TpmStructure
{
    /**
     * TPMS_AC_OUTPUT is used to return information about an AC. The tag structure parameter indicates the type of the data value.
     * 
     * @param _tag tag indicating the contents of data 
     * @param _data the data returned from the AC
     */
    public TPMS_AC_OUTPUT(TPM_AT _tag,int _data)
    {
        tag = _tag;
        data = _data;
    }
    /**
    * TPMS_AC_OUTPUT is used to return information about an AC. The tag structure parameter indicates the type of the data value.
    */
    public TPMS_AC_OUTPUT() {};
    /**
    * tag indicating the contents of data
    */
    public TPM_AT tag;
    /**
    * the data returned from the AC
    */
    public int data;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        tag.toTpm(buf);
        buf.write(data);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        tag = TPM_AT.fromTpm(buf);
        data =  buf.readInt(4);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_AC_OUTPUT fromTpm (byte[] x) 
    {
        TPMS_AC_OUTPUT ret = new TPMS_AC_OUTPUT();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_AC_OUTPUT fromTpm (InByteBuf buf) 
    {
        TPMS_AC_OUTPUT ret = new TPMS_AC_OUTPUT();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_AC_OUTPUT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_AT", "tag", tag);
        _p.add(d, "uint", "data", data);
    };
    
    
};

//<<<

