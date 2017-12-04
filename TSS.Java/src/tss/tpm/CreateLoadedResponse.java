package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command creates an object and loads it in the TPM. This command allows creation of any type of object (Primary, Ordinary, or Derived) depending on the type of parentHandle. If parentHandle references a Primary Seed, then a Primary Object is created; if parentHandle references a Storage Parent, then an Ordinary Object is created; and if parentHandle references a Derivation Parent, then a Derived Object is generated.
*/
public class CreateLoadedResponse extends TpmStructure
{
    /**
     * This command creates an object and loads it in the TPM. This command allows creation of any type of object (Primary, Ordinary, or Derived) depending on the type of parentHandle. If parentHandle references a Primary Seed, then a Primary Object is created; if parentHandle references a Storage Parent, then an Ordinary Object is created; and if parentHandle references a Derivation Parent, then a Derived Object is generated.
     * 
     * @param _handle handle of type TPM_HT_TRANSIENT for created object 
     * @param _outPrivate the sensitive area of the object (optional) 
     * @param _outPublic the public portion of the created object 
     * @param _name the name of the created object
     */
    public CreateLoadedResponse(TPM_HANDLE _handle,TPM2B_PRIVATE _outPrivate,TPMT_PUBLIC _outPublic,byte[] _name)
    {
        handle = _handle;
        outPrivate = _outPrivate;
        outPublic = _outPublic;
        name = _name;
    }
    /**
    * This command creates an object and loads it in the TPM. This command allows creation of any type of object (Primary, Ordinary, or Derived) depending on the type of parentHandle. If parentHandle references a Primary Seed, then a Primary Object is created; if parentHandle references a Storage Parent, then an Ordinary Object is created; and if parentHandle references a Derivation Parent, then a Derived Object is generated.
    */
    public CreateLoadedResponse() {};
    /**
    * handle of type TPM_HT_TRANSIENT for created object
    */
    public TPM_HANDLE handle;
    /**
    * the sensitive area of the object (optional)
    */
    public TPM2B_PRIVATE outPrivate;
    /**
    * size of publicArea NOTE The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.
    */
    // private short outPublicSize;
    /**
    * the public portion of the created object
    */
    public TPMT_PUBLIC outPublic;
    /**
    * size of the Name structure
    */
    // private short nameSize;
    /**
    * the name of the created object
    */
    public byte[] name;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        outPrivate.toTpm(buf);
        buf.writeInt((outPublic!=null)?outPublic.toTpm().length:0, 2);
        if(outPublic!=null)
            outPublic.toTpm(buf);
        buf.writeInt((name!=null)?name.length:0, 2);
        if(name!=null)
            buf.write(name);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        outPrivate = TPM2B_PRIVATE.fromTpm(buf);
        int _outPublicSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _outPublicSize));
        outPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
        int _nameSize = buf.readInt(2);
        name = new byte[_nameSize];
        buf.readArrayOfInts(name, 1, _nameSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static CreateLoadedResponse fromTpm (byte[] x) 
    {
        CreateLoadedResponse ret = new CreateLoadedResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static CreateLoadedResponse fromTpm (InByteBuf buf) 
    {
        CreateLoadedResponse ret = new CreateLoadedResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CreateLoaded_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "TPM2B_PRIVATE", "outPrivate", outPrivate);
        _p.add(d, "TPMT_PUBLIC", "outPublic", outPublic);
        _p.add(d, "byte", "name", name);
    };
    
    
};

//<<<

