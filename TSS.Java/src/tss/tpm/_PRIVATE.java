package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is defined to size the contents of a TPM2B_PRIVATE. This structure is not directly marshaled or unmarshaled.
*/
public class _PRIVATE extends TpmStructure
{
    /**
     * This structure is defined to size the contents of a TPM2B_PRIVATE. This structure is not directly marshaled or unmarshaled.
     * 
     * @param _integrityOuter - 
     * @param _integrityInner could also be a TPM2B_IV 
     * @param _sensitive the sensitive area
     */
    public _PRIVATE(byte[] _integrityOuter,byte[] _integrityInner,TPMT_SENSITIVE _sensitive)
    {
        integrityOuter = _integrityOuter;
        integrityInner = _integrityInner;
        sensitive = _sensitive;
    }
    /**
    * This structure is defined to size the contents of a TPM2B_PRIVATE. This structure is not directly marshaled or unmarshaled.
    */
    public _PRIVATE() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short integrityOuterSize;
    public byte[] integrityOuter;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short integrityInnerSize;
    /**
    * could also be a TPM2B_IV
    */
    public byte[] integrityInner;
    /**
    * size of the private structure
    */
    // private short sensitiveSize;
    /**
    * the sensitive area
    */
    public TPMT_SENSITIVE sensitive;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((integrityOuter!=null)?integrityOuter.length:0, 2);
        if(integrityOuter!=null)
            buf.write(integrityOuter);
        buf.writeInt((integrityInner!=null)?integrityInner.length:0, 2);
        if(integrityInner!=null)
            buf.write(integrityInner);
        buf.writeInt((sensitive!=null)?sensitive.toTpm().length:0, 2);
        if(sensitive!=null)
            sensitive.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _integrityOuterSize = buf.readInt(2);
        integrityOuter = new byte[_integrityOuterSize];
        buf.readArrayOfInts(integrityOuter, 1, _integrityOuterSize);
        int _integrityInnerSize = buf.readInt(2);
        integrityInner = new byte[_integrityInnerSize];
        buf.readArrayOfInts(integrityInner, 1, _integrityInnerSize);
        int _sensitiveSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _sensitiveSize));
        sensitive = TPMT_SENSITIVE.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static _PRIVATE fromTpm (byte[] x) 
    {
        _PRIVATE ret = new _PRIVATE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static _PRIVATE fromTpm (InByteBuf buf) 
    {
        _PRIVATE ret = new _PRIVATE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("_PRIVATE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "integrityOuter", integrityOuter);
        _p.add(d, "byte", "integrityInner", integrityInner);
        _p.add(d, "TPMT_SENSITIVE", "sensitive", sensitive);
    };
    
    
};

//<<<

