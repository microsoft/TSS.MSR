package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This is the attested data for TPM2_Certify().
*/
public class TPMS_CERTIFY_INFO extends TpmStructure implements TPMU_ATTEST 
{
    /**
     * This is the attested data for TPM2_Certify().
     * 
     * @param _name Name of the certified object 
     * @param _qualifiedName Qualified Name of the certified object
     */
    public TPMS_CERTIFY_INFO(byte[] _name,byte[] _qualifiedName)
    {
        name = _name;
        qualifiedName = _qualifiedName;
    }
    /**
    * This is the attested data for TPM2_Certify().
    */
    public TPMS_CERTIFY_INFO() {};
    /**
    * size of the Name structure
    */
    // private short nameSize;
    /**
    * Name of the certified object
    */
    public byte[] name;
    /**
    * size of the Name structure
    */
    // private short qualifiedNameSize;
    /**
    * Qualified Name of the certified object
    */
    public byte[] qualifiedName;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
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
    public static TPMS_CERTIFY_INFO fromTpm (byte[] x) 
    {
        TPMS_CERTIFY_INFO ret = new TPMS_CERTIFY_INFO();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_CERTIFY_INFO fromTpm (InByteBuf buf) 
    {
        TPMS_CERTIFY_INFO ret = new TPMS_CERTIFY_INFO();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CERTIFY_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "name", name);
        _p.add(d, "byte", "qualifiedName", qualifiedName);
    };
    
    
};

//<<<

