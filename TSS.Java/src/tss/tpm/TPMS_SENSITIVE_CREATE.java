package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure defines the values to be placed in the sensitive area of a created object. This structure is only used within a TPM2B_SENSITIVE_CREATE structure.
*/
public class TPMS_SENSITIVE_CREATE extends TpmStructure
{
    /**
     * This structure defines the values to be placed in the sensitive area of a created object. This structure is only used within a TPM2B_SENSITIVE_CREATE structure.
     * 
     * @param _userAuth the USER auth secret value 
     * @param _data data to be sealed, a key, or derivation values
     */
    public TPMS_SENSITIVE_CREATE(byte[] _userAuth,byte[] _data)
    {
        userAuth = _userAuth;
        data = _data;
    }
    /**
    * This structure defines the values to be placed in the sensitive area of a created object. This structure is only used within a TPM2B_SENSITIVE_CREATE structure.
    */
    public TPMS_SENSITIVE_CREATE() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short userAuthSize;
    /**
    * the USER auth secret value
    */
    public byte[] userAuth;
    // private short dataSize;
    /**
    * data to be sealed, a key, or derivation values
    */
    public byte[] data;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((userAuth!=null)?userAuth.length:0, 2);
        if(userAuth!=null)
            buf.write(userAuth);
        buf.writeInt((data!=null)?data.length:0, 2);
        if(data!=null)
            buf.write(data);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _userAuthSize = buf.readInt(2);
        userAuth = new byte[_userAuthSize];
        buf.readArrayOfInts(userAuth, 1, _userAuthSize);
        int _dataSize = buf.readInt(2);
        data = new byte[_dataSize];
        buf.readArrayOfInts(data, 1, _dataSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_SENSITIVE_CREATE fromTpm (byte[] x) 
    {
        TPMS_SENSITIVE_CREATE ret = new TPMS_SENSITIVE_CREATE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
    };
    
    
};

//<<<

