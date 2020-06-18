package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to load an object that is not a Protected Object into the TPM.
 *  The command allows loading of a public area or both a public and sensitive area.
 */
public class LoadExternalResponse extends TpmStructure
{
    /** Handle of type TPM_HT_TRANSIENT for the loaded object  */
    public TPM_HANDLE handle;
    
    /** Name of the loaded object  */
    public byte[] name;
    
    public LoadExternalResponse() { handle = new TPM_HANDLE(); }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        buf.writeSizedByteBuf(name);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        int _nameSize = buf.readShort() & 0xFFFF;
        name = new byte[_nameSize];
        buf.readArrayOfInts(name, 1, _nameSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static LoadExternalResponse fromBytes (byte[] byteBuf) 
    {
        LoadExternalResponse ret = new LoadExternalResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static LoadExternalResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
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
    }
}

//<<<
