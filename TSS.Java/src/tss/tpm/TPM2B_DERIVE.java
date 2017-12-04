package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 137 Definition of TPM2B_DERIVE Structure
*/
public class TPM2B_DERIVE extends TpmStructure
{
    /**
     * Table 137 Definition of TPM2B_DERIVE Structure
     * 
     * @param _buffer symmetic data for a created object or the label and context for a derived object
     */
    public TPM2B_DERIVE(TPMS_DERIVE _buffer)
    {
        buffer = _buffer;
    }
    /**
    * Table 137 Definition of TPM2B_DERIVE Structure
    */
    public TPM2B_DERIVE() {};
    // private short size;
    /**
    * symmetic data for a created object or the label and context for a derived object
    */
    public TPMS_DERIVE buffer;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((buffer!=null)?buffer.toTpm().length:0, 2);
        if(buffer!=null)
            buffer.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _size));
        buffer = TPMS_DERIVE.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2B_DERIVE fromTpm (byte[] x) 
    {
        TPM2B_DERIVE ret = new TPM2B_DERIVE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_DERIVE fromTpm (InByteBuf buf) 
    {
        TPM2B_DERIVE ret = new TPM2B_DERIVE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_DERIVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_DERIVE", "buffer", buffer);
    };
    
    
};

//<<<

