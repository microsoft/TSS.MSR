package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Contains the public and the plaintext-sensitive and/or encrypted private part of a TPM
 *  key (or other object)
 */
public class TssObject extends TpmStructure
{
    /** Public part of key  */
    public TPMT_PUBLIC Public;
    
    /** Sensitive part of key  */
    public TPMT_SENSITIVE Sensitive;
    
    /** Private part is the encrypted sensitive part of key  */
    public TPM2B_PRIVATE Private;
    
    public TssObject() {}
    
    /** @param _Public Public part of key
     *  @param _Sensitive Sensitive part of key
     *  @param _Private Private part is the encrypted sensitive part of key
     */
    public TssObject(TPMT_PUBLIC _Public, TPMT_SENSITIVE _Sensitive, TPM2B_PRIVATE _Private)
    {
        Public = _Public;
        Sensitive = _Sensitive;
        Private = _Private;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        Public.toTpm(buf);
        Sensitive.toTpm(buf);
        Private.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        Public = TPMT_PUBLIC.fromTpm(buf);
        Sensitive = TPMT_SENSITIVE.fromTpm(buf);
        Private = TPM2B_PRIVATE.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TssObject fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TssObject.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TssObject fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TssObject fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TssObject.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TssObject");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_PUBLIC", "Public", Public);
        _p.add(d, "TPMT_SENSITIVE", "Sensitive", Sensitive);
        _p.add(d, "TPM2B_PRIVATE", "Private", Private);
    }
}

//<<<
