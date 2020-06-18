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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(userAuth);
        buf.writeSizedByteBuf(data);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _userAuthSize = buf.readShort() & 0xFFFF;
        userAuth = new byte[_userAuthSize];
        buf.readArrayOfInts(userAuth, 1, _userAuthSize);
        int _dataSize = buf.readShort() & 0xFFFF;
        data = new byte[_dataSize];
        buf.readArrayOfInts(data, 1, _dataSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMS_SENSITIVE_CREATE fromBytes (byte[] byteBuf) 
    {
        TPMS_SENSITIVE_CREATE ret = new TPMS_SENSITIVE_CREATE();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SENSITIVE_CREATE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPMS_SENSITIVE_CREATE fromTpm (InByteBuf buf) 
    {
        TPMS_SENSITIVE_CREATE ret = new TPMS_SENSITIVE_CREATE();
        ret.initFromTpm(buf);
        return ret;
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
