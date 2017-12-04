package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used in a TPMS_CONTEXT.
*/
public class TPM2B_CONTEXT_DATA extends TpmStructure
{
    /**
     * This structure is used in a TPMS_CONTEXT.
     * 
     * @param _buffer -
     */
    public TPM2B_CONTEXT_DATA(TPMS_CONTEXT_DATA _buffer)
    {
        buffer = _buffer;
    }
    /**
    * This structure is used in a TPMS_CONTEXT.
    */
    public TPM2B_CONTEXT_DATA() {};
    // private short size;
    public TPMS_CONTEXT_DATA buffer;
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
        buffer = TPMS_CONTEXT_DATA.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2B_CONTEXT_DATA fromTpm (byte[] x) 
    {
        TPM2B_CONTEXT_DATA ret = new TPM2B_CONTEXT_DATA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_CONTEXT_DATA fromTpm (InByteBuf buf) 
    {
        TPM2B_CONTEXT_DATA ret = new TPM2B_CONTEXT_DATA();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_CONTEXT_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_CONTEXT_DATA", "buffer", buffer);
    };
    
    
};

//<<<

