package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure defines the values to be placed in the sensitive area of a created
 *  object. This structure is only used within a TPM2B_SENSITIVE_CREATE structure.
 */
public class TPMS_SENSITIVE_CREATE extends TpmStructure
{
    /** The USER auth secret value  */
    public byte[] userAuth;
    
    /** Data to be sealed, a key, or derivation values  */
    public byte[] data;
    
    public TPMS_SENSITIVE_CREATE() {}
    
    /** @param _userAuth The USER auth secret value
     *  @param _data Data to be sealed, a key, or derivation values
     */
    public TPMS_SENSITIVE_CREATE(byte[] _userAuth, byte[] _data)
    {
        userAuth = _userAuth;
        data = _data;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(userAuth);
        buf.writeSizedByteBuf(data);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        userAuth = buf.readSizedByteBuf();
        data = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_SENSITIVE_CREATE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SENSITIVE_CREATE.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SENSITIVE_CREATE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_SENSITIVE_CREATE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SENSITIVE_CREATE.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SENSITIVE_CREATE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "userAuth", userAuth);
        _p.add(d, "byte", "data", data);
    }
}

//<<<
