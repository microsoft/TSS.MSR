package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Contains the public and private part of a TPM key
*/
public class TSS_KEY extends TpmStructure
{
    /**
     * Contains the public and private part of a TPM key
     * 
     * @param _publicPart Public part of key 
     * @param _privatePart Private part is the encrypted sensitive part of key
     */
    public TSS_KEY(TPMT_PUBLIC _publicPart,byte[] _privatePart)
    {
        publicPart = _publicPart;
        privatePart = _privatePart;
    }
    /**
    * Contains the public and private part of a TPM key
    */
    public TSS_KEY() {};
    /**
    * Public part of key
    */
    public TPMT_PUBLIC publicPart;
    // private short privatePartSize;
    /**
    * Private part is the encrypted sensitive part of key
    */
    public byte[] privatePart;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        publicPart.toTpm(buf);
        buf.writeInt((privatePart!=null)?privatePart.length:0, 2);
        if(privatePart!=null)
            buf.write(privatePart);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        publicPart = TPMT_PUBLIC.fromTpm(buf);
        int _privatePartSize = buf.readInt(2);
        privatePart = new byte[_privatePartSize];
        buf.readArrayOfInts(privatePart, 1, _privatePartSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TSS_KEY fromTpm (byte[] x) 
    {
        TSS_KEY ret = new TSS_KEY();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TSS_KEY fromTpm (InByteBuf buf) 
    {
        TSS_KEY ret = new TSS_KEY();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TSS_KEY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_PUBLIC", "publicPart", publicPart);
        _p.add(d, "byte", "privatePart", privatePart);
    };
    
    
};

//<<<

