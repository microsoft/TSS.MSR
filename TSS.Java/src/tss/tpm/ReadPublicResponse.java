package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows access to the public area of a loaded object.
*/
public class ReadPublicResponse extends TpmStructure
{
    /**
     * This command allows access to the public area of a loaded object.
     * 
     * @param _outPublic structure containing the public area of an object 
     * @param _name name of the object 
     * @param _qualifiedName the Qualified Name of the object
     */
    public ReadPublicResponse(TPMT_PUBLIC _outPublic,byte[] _name,byte[] _qualifiedName)
    {
        outPublic = _outPublic;
        name = _name;
        qualifiedName = _qualifiedName;
    }
    /**
    * This command allows access to the public area of a loaded object.
    */
    public ReadPublicResponse() {};
    /**
    * size of publicArea NOTE The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.
    */
    // private short outPublicSize;
    /**
    * structure containing the public area of an object
    */
    public TPMT_PUBLIC outPublic;
    /**
    * size of the Name structure
    */
    // private short nameSize;
    /**
    * name of the object
    */
    public byte[] name;
    /**
    * size of the Name structure
    */
    // private short qualifiedNameSize;
    /**
    * the Qualified Name of the object
    */
    public byte[] qualifiedName;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((outPublic!=null)?outPublic.toTpm().length:0, 2);
        if(outPublic!=null)
            outPublic.toTpm(buf);
        buf.writeInt((name!=null)?name.length:0, 2);
        if(name!=null)
            buf.write(name);
        buf.writeInt((qualifiedName!=null)?qualifiedName.length:0, 2);
        if(qualifiedName!=null)
            buf.write(qualifiedName);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _outPublicSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _outPublicSize));
        outPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
        int _nameSize = buf.readInt(2);
        name = new byte[_nameSize];
        buf.readArrayOfInts(name, 1, _nameSize);
        int _qualifiedNameSize = buf.readInt(2);
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
    };
    
    
};

//<<<

