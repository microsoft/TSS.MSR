package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to load an object that is not a Protected Object into the TPM. The command allows loading of a public area or both a public and sensitive area.
*/
public class LoadExternalResponse extends TpmStructure
{
    /**
     * This command is used to load an object that is not a Protected Object into the TPM. The command allows loading of a public area or both a public and sensitive area.
     * 
     * @param _handle handle of type TPM_HT_TRANSIENT for the loaded object 
     * @param _name name of the loaded object
     */
    public LoadExternalResponse(TPM_HANDLE _handle,byte[] _name)
    {
        handle = _handle;
        name = _name;
    }
    /**
    * This command is used to load an object that is not a Protected Object into the TPM. The command allows loading of a public area or both a public and sensitive area.
    */
    public LoadExternalResponse() {};
    /**
    * handle of type TPM_HT_TRANSIENT for the loaded object
    */
    public TPM_HANDLE handle;
    /**
    * size of the Name structure
    */
    // private short nameSize;
    /**
    * name of the loaded object
    */
    public byte[] name;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        buf.writeInt((name!=null)?name.length:0, 2);
        if(name!=null)
            buf.write(name);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
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
    public static LoadExternalResponse fromTpm (byte[] x) 
    {
        LoadExternalResponse ret = new LoadExternalResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static LoadExternalResponse fromTpm (InByteBuf buf) 
    {
        LoadExternalResponse ret = new LoadExternalResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_LoadExternal_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte", "name", name);
    };
    
    
};

//<<<

