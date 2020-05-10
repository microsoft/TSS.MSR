package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows access to the public area of a loaded object. */
public class ReadPublicResponse extends TpmStructure
{
    /** structure containing the public area of an object */
    public TPMT_PUBLIC outPublic;
    
    /** name of the object */
    public byte[] name;
    
    /** the Qualified Name of the object */
    public byte[] qualifiedName;
    
    public ReadPublicResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeShort(outPublic != null ? outPublic.toTpm().length : 0);
        if (outPublic != null)
            outPublic.toTpm(buf);
        buf.writeSizedByteBuf(name);
        buf.writeSizedByteBuf(qualifiedName);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outPublicSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _outPublicSize));
        outPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
        int _nameSize = buf.readShort() & 0xFFFF;
        name = new byte[_nameSize];
        buf.readArrayOfInts(name, 1, _nameSize);
        int _qualifiedNameSize = buf.readShort() & 0xFFFF;
        qualifiedName = new byte[_qualifiedNameSize];
        buf.readArrayOfInts(qualifiedName, 1, _qualifiedNameSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static ReadPublicResponse fromTpm (byte[] x) 
    {
        ReadPublicResponse ret = new ReadPublicResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static ReadPublicResponse fromTpm (InByteBuf buf) 
    {
        ReadPublicResponse ret = new ReadPublicResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ReadPublic_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_PUBLIC", "outPublic", outPublic);
        _p.add(d, "byte", "name", name);
        _p.add(d, "byte", "qualifiedName", qualifiedName);
    }
}

//<<<

