package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The TPM2B_SENSITIVE structure is used as a parameter in TPM2_LoadExternal(). It is an
 *  unencrypted sensitive area but it may be encrypted using parameter encryption.
 */
public class TPM2B_SENSITIVE extends TpmStructure
{
    /** An unencrypted sensitive area  */
    public TPMT_SENSITIVE sensitiveArea;
    
    public TPM2B_SENSITIVE() {}
    
    /** @param _sensitiveArea An unencrypted sensitive area  */
    public TPM2B_SENSITIVE(TPMT_SENSITIVE _sensitiveArea) { sensitiveArea = _sensitiveArea; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(sensitiveArea); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { sensitiveArea = buf.createSizedObj(TPMT_SENSITIVE.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_SENSITIVE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_SENSITIVE.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_SENSITIVE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_SENSITIVE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_SENSITIVE.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_SENSITIVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_SENSITIVE", "sensitiveArea", sensitiveArea);
    }
}

//<<<
