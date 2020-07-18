package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to change the authorization secret for a TPM-resident object.  */
public class ObjectChangeAuthResponse extends RespStructure
{
    /** Private area containing the new authorization value  */
    public TPM2B_PRIVATE outPrivate;
    
    public ObjectChangeAuthResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { outPrivate.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { outPrivate = TPM2B_PRIVATE.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static ObjectChangeAuthResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ObjectChangeAuthResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ObjectChangeAuthResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static ObjectChangeAuthResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ObjectChangeAuthResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("ObjectChangeAuthResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM2B_PRIVATE", "outPrivate", outPrivate);
    }
}

//<<<
