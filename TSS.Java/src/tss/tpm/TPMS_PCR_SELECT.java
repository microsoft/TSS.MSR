package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure provides a standard method of specifying a list of PCR.
*/
public class TPMS_PCR_SELECT extends TpmStructure
{
    /**
     * This structure provides a standard method of specifying a list of PCR.
     * 
     * @param _pcrSelect the bit map of selected PCR
     */
    public TPMS_PCR_SELECT(byte[] _pcrSelect)
    {
        pcrSelect = _pcrSelect;
    }
    /**
    * This structure provides a standard method of specifying a list of PCR.
    */
    public TPMS_PCR_SELECT() {};
    /**
    * the size in octets of the pcrSelect array
    */
    // private byte sizeofSelect;
    /**
    * the bit map of selected PCR
    */
    public byte[] pcrSelect;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((pcrSelect!=null)?pcrSelect.length:0, 1);
        if(pcrSelect!=null)
            buf.write(pcrSelect);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _sizeofSelect = buf.readInt(1);
        pcrSelect = new byte[_sizeofSelect];
        buf.readArrayOfInts(pcrSelect, 1, _sizeofSelect);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_PCR_SELECT fromTpm (byte[] x) 
    {
        TPMS_PCR_SELECT ret = new TPMS_PCR_SELECT();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_PCR_SELECT fromTpm (InByteBuf buf) 
    {
        TPMS_PCR_SELECT ret = new TPMS_PCR_SELECT();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_PCR_SELECT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "pcrSelect", pcrSelect);
    };
    
    
};

//<<<

