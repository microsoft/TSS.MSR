package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Contains a PCR index and associated hash(pcr-value) [TSS]
*/
public class PcrValue extends TpmStructure
{
    /**
     * Contains a PCR index and associated hash(pcr-value) [TSS]
     * 
     * @param _index PCR Index 
     * @param _value PCR Value
     */
    public PcrValue(int _index,TPMT_HA _value)
    {
        index = _index;
        value = _value;
    }
    /**
    * Contains a PCR index and associated hash(pcr-value) [TSS]
    */
    public PcrValue() {};
    /**
    * PCR Index
    */
    public int index;
    /**
    * PCR Value
    */
    public TPMT_HA value;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.write(index);
        value.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        index =  buf.readInt(4);
        // TODO TpmHash  -- 
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static PcrValue fromTpm (byte[] x) 
    {
        PcrValue ret = new PcrValue();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PcrValue fromTpm (InByteBuf buf) 
    {
        PcrValue ret = new PcrValue();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("PcrValue");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "uint", "index", index);
        _p.add(d, "TpmHash", "value", value);
    };
    
    
};

//<<<

