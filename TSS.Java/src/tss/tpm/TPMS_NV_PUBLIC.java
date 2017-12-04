package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure describes an NV Index.
*/
public class TPMS_NV_PUBLIC extends TpmStructure
{
    /**
     * This structure describes an NV Index.
     * 
     * @param _nvIndex the handle of the data area 
     * @param _nameAlg hash algorithm used to compute the name of the Index and used for the authPolicy. For an extend index, the hash algorithm used for the extend. 
     * @param _attributes the Index attributes 
     * @param _authPolicy optional access policy for the Index The policy is computed using the nameAlg NOTE Shall be the Empty Policy if no authorization policy is present. 
     * @param _dataSize the size of the data area The maximum size is implementation-dependent. The minimum maximum size is platform-specific.
     */
    public TPMS_NV_PUBLIC(TPM_HANDLE _nvIndex,TPM_ALG_ID _nameAlg,TPMA_NV _attributes,byte[] _authPolicy,int _dataSize)
    {
        nvIndex = _nvIndex;
        nameAlg = _nameAlg;
        attributes = _attributes;
        authPolicy = _authPolicy;
        dataSize = (short)_dataSize;
    }
    /**
    * This structure describes an NV Index.
    */
    public TPMS_NV_PUBLIC() {};
    /**
    * the handle of the data area
    */
    public TPM_HANDLE nvIndex;
    /**
    * hash algorithm used to compute the name of the Index and used for the authPolicy. For an extend index, the hash algorithm used for the extend.
    */
    public TPM_ALG_ID nameAlg;
    /**
    * the Index attributes
    */
    public TPMA_NV attributes;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short authPolicySize;
    /**
    * optional access policy for the Index The policy is computed using the nameAlg NOTE Shall be the Empty Policy if no authorization policy is present.
    */
    public byte[] authPolicy;
    /**
    * the size of the data area The maximum size is implementation-dependent. The minimum maximum size is platform-specific.
    */
    public short dataSize;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        nvIndex.toTpm(buf);
        nameAlg.toTpm(buf);
        attributes.toTpm(buf);
        buf.writeInt((authPolicy!=null)?authPolicy.length:0, 2);
        if(authPolicy!=null)
            buf.write(authPolicy);
        buf.write(dataSize);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        nvIndex = TPM_HANDLE.fromTpm(buf);
        nameAlg = TPM_ALG_ID.fromTpm(buf);
        int _attributes = buf.readInt(4);
        attributes = TPMA_NV.fromInt(_attributes);
        int _authPolicySize = buf.readInt(2);
        authPolicy = new byte[_authPolicySize];
        buf.readArrayOfInts(authPolicy, 1, _authPolicySize);
        dataSize = (short) buf.readInt(2);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_NV_PUBLIC fromTpm (byte[] x) 
    {
        TPMS_NV_PUBLIC ret = new TPMS_NV_PUBLIC();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_NV_PUBLIC fromTpm (InByteBuf buf) 
    {
        TPMS_NV_PUBLIC ret = new TPMS_NV_PUBLIC();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NV_PUBLIC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "TPM_ALG_ID", "nameAlg", nameAlg);
        _p.add(d, "TPMA_NV", "attributes", attributes);
        _p.add(d, "byte", "authPolicy", authPolicy);
        _p.add(d, "ushort", "dataSize", dataSize);
    };
    
    
};

//<<<

