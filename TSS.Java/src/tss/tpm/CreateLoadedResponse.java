package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command creates an object and loads it in the TPM. This command allows creation
 *  of any type of object (Primary, Ordinary, or Derived) depending on the type of
 *  parentHandle. If parentHandle references a Primary Seed, then a Primary Object is
 *  created; if parentHandle references a Storage Parent, then an Ordinary Object is
 *  created; and if parentHandle references a Derivation Parent, then a Derived Object is generated.
 */
public class CreateLoadedResponse extends RespStructure
{
    /** Handle of type TPM_HT_TRANSIENT for created object  */
    public TPM_HANDLE handle;
    
    /** The sensitive area of the object (optional)  */
    public TPM2B_PRIVATE outPrivate;
    
    /** The public portion of the created object  */
    public TPMT_PUBLIC outPublic;
    
    /** The name of the created object  */
    public byte[] name;
    
    public CreateLoadedResponse() { handle = new TPM_HANDLE(); }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        outPrivate.toTpm(buf);
        buf.writeSizedObj(outPublic);
        buf.writeSizedByteBuf(name);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        outPrivate = TPM2B_PRIVATE.fromTpm(buf);
        outPublic = buf.createSizedObj(TPMT_PUBLIC.class);
        name = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static CreateLoadedResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(CreateLoadedResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static CreateLoadedResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static CreateLoadedResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(CreateLoadedResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("CreateLoadedResponse");
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
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public TPM_HANDLE getHandle() { return handle; }

    @Override
    public void setHandle(TPM_HANDLE h) { handle = h; }
}

//<<<
