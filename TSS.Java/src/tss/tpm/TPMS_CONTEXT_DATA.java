package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure holds the integrity value and the encrypted data for a context.
*/
public class TPMS_CONTEXT_DATA extends TpmStructure
{
    /**
     * This structure holds the integrity value and the encrypted data for a context.
     * 
     * @param _integrity the integrity value 
     * @param _encrypted the sensitive area
     */
    public TPMS_CONTEXT_DATA(byte[] _integrity,byte[] _encrypted)
    {
        integrity = _integrity;
        encrypted = _encrypted;
    }
    /**
    * This structure holds the integrity value and the encrypted data for a context.
    */
    public TPMS_CONTEXT_DATA() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short integritySize;
    /**
    * the integrity value
    */
    public byte[] integrity;
    /**
    * the sensitive area
    */
    public byte[] encrypted;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((integrity!=null)?integrity.length:0, 2);
        if(integrity!=null)
            buf.write(integrity);
        buf.write(encrypted);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _integritySize = buf.readInt(2);
        integrity = new byte[_integritySize];
        buf.readArrayOfInts(integrity, 1, _integritySize);
        InByteBuf.SizedStructInfo si = buf.structSize.peek();
        int _encryptedSize = si.Size - (buf.curPos() - si.StartPos);
        encrypted = new byte[_encryptedSize];
        buf.readArrayOfInts(encrypted, 1, _encryptedSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_CONTEXT_DATA fromTpm (byte[] x) 
    {
        TPMS_CONTEXT_DATA ret = new TPMS_CONTEXT_DATA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_CONTEXT_DATA fromTpm (InByteBuf buf) 
    {
        TPMS_CONTEXT_DATA ret = new TPMS_CONTEXT_DATA();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CONTEXT_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "integrity", integrity);
        _p.add(d, "byte", "encrypted", encrypted);
    };
    
    
};

//<<<

