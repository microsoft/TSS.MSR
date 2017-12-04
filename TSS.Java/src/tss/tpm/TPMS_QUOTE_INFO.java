package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This is the attested data for TPM2_Quote().
*/
public class TPMS_QUOTE_INFO extends TpmStructure implements TPMU_ATTEST 
{
    /**
     * This is the attested data for TPM2_Quote().
     * 
     * @param _pcrSelect information on algID, PCR selected and digest 
     * @param _pcrDigest digest of the selected PCR using the hash of the signing key
     */
    public TPMS_QUOTE_INFO(TPMS_PCR_SELECTION[] _pcrSelect,byte[] _pcrDigest)
    {
        pcrSelect = _pcrSelect;
        pcrDigest = _pcrDigest;
    }
    /**
    * This is the attested data for TPM2_Quote().
    */
    public TPMS_QUOTE_INFO() {};
    /**
    * number of selection structures A value of zero is allowed.
    */
    // private int pcrSelectCount;
    /**
    * information on algID, PCR selected and digest
    */
    public TPMS_PCR_SELECTION[] pcrSelect;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short pcrDigestSize;
    /**
    * digest of the selected PCR using the hash of the signing key
    */
    public byte[] pcrDigest;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((pcrSelect!=null)?pcrSelect.length:0, 4);
        if(pcrSelect!=null)
            buf.writeArrayOfTpmObjects(pcrSelect);
        buf.writeInt((pcrDigest!=null)?pcrDigest.length:0, 2);
        if(pcrDigest!=null)
            buf.write(pcrDigest);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _pcrSelectCount = buf.readInt(4);
        pcrSelect = new TPMS_PCR_SELECTION[_pcrSelectCount];
        for(int j=0;j<_pcrSelectCount;j++)pcrSelect[j]=new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(pcrSelect, _pcrSelectCount);
        int _pcrDigestSize = buf.readInt(2);
        pcrDigest = new byte[_pcrDigestSize];
        buf.readArrayOfInts(pcrDigest, 1, _pcrDigestSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_QUOTE_INFO fromTpm (byte[] x) 
    {
        TPMS_QUOTE_INFO ret = new TPMS_QUOTE_INFO();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_QUOTE_INFO fromTpm (InByteBuf buf) 
    {
        TPMS_QUOTE_INFO ret = new TPMS_QUOTE_INFO();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_QUOTE_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_PCR_SELECTION", "pcrSelect", pcrSelect);
        _p.add(d, "byte", "pcrDigest", pcrDigest);
    };
    
    
};

//<<<

