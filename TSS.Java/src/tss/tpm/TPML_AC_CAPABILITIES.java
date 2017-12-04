package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This list is only used in TPM2_AC_GetCapability().
*/
public class TPML_AC_CAPABILITIES extends TpmStructure
{
    /**
     * This list is only used in TPM2_AC_GetCapability().
     * 
     * @param _acCapabilities a list of AC values
     */
    public TPML_AC_CAPABILITIES(TPMS_AC_OUTPUT[] _acCapabilities)
    {
        acCapabilities = _acCapabilities;
    }
    /**
    * This list is only used in TPM2_AC_GetCapability().
    */
    public TPML_AC_CAPABILITIES() {};
    /**
    * number of values in the acCapabilities list; may be 0
    */
    // private int count;
    /**
    * a list of AC values
    */
    public TPMS_AC_OUTPUT[] acCapabilities;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((acCapabilities!=null)?acCapabilities.length:0, 4);
        if(acCapabilities!=null)
            buf.writeArrayOfTpmObjects(acCapabilities);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _count = buf.readInt(4);
        acCapabilities = new TPMS_AC_OUTPUT[_count];
        for(int j=0;j<_count;j++)acCapabilities[j]=new TPMS_AC_OUTPUT();
        buf.readArrayOfTpmObjects(acCapabilities, _count);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPML_AC_CAPABILITIES fromTpm (byte[] x) 
    {
        TPML_AC_CAPABILITIES ret = new TPML_AC_CAPABILITIES();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPML_AC_CAPABILITIES fromTpm (InByteBuf buf) 
    {
        TPML_AC_CAPABILITIES ret = new TPML_AC_CAPABILITIES();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_AC_CAPABILITIES");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_AC_OUTPUT", "acCapabilities", acCapabilities);
    };
    
    
};

//<<<

