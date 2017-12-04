package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure contains the label and context fields for a derived object. These values are used in the derivation KDF. The values in the unique field of inPublic area template take precedence over the values in the inSensitive parameter.
*/
public class TPMS_DERIVE extends TpmStructure implements TPMU_SENSITIVE_CREATE, TPMU_PUBLIC_ID 
{
    /**
     * This structure contains the label and context fields for a derived object. These values are used in the derivation KDF. The values in the unique field of inPublic area template take precedence over the values in the inSensitive parameter.
     * 
     * @param _label - 
     * @param _context -
     */
    public TPMS_DERIVE(byte[] _label,byte[] _context)
    {
        label = _label;
        context = _context;
    }
    /**
    * This structure contains the label and context fields for a derived object. These values are used in the derivation KDF. The values in the unique field of inPublic area template take precedence over the values in the inSensitive parameter.
    */
    public TPMS_DERIVE() {};
    // private short labelSize;
    public byte[] label;
    // private short contextSize;
    public byte[] context;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((label!=null)?label.length:0, 2);
        if(label!=null)
            buf.write(label);
        buf.writeInt((context!=null)?context.length:0, 2);
        if(context!=null)
            buf.write(context);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _labelSize = buf.readInt(2);
        label = new byte[_labelSize];
        buf.readArrayOfInts(label, 1, _labelSize);
        int _contextSize = buf.readInt(2);
        context = new byte[_contextSize];
        buf.readArrayOfInts(context, 1, _contextSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_DERIVE fromTpm (byte[] x) 
    {
        TPMS_DERIVE ret = new TPMS_DERIVE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_DERIVE fromTpm (InByteBuf buf) 
    {
        TPMS_DERIVE ret = new TPMS_DERIVE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_DERIVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "label", label);
        _p.add(d, "byte", "context", context);
    };
    
    
};

//<<<

