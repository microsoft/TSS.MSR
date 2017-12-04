package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 87 Definition of TPMS_PCR_SELECTION Structure
*/
public class TPMS_PCR_SELECTION extends TpmStructure
{
    /**
     * Table 87 Definition of TPMS_PCR_SELECTION Structure
     * 
     * @param _hash the hash algorithm associated with the selection 
     * @param _pcrSelect the bit map of selected PCR
     */
    public TPMS_PCR_SELECTION(TPM_ALG_ID _hash,byte[] _pcrSelect)
    {
        hash = _hash;
        pcrSelect = _pcrSelect;
    }
    /**
    * Table 87 Definition of TPMS_PCR_SELECTION Structure
    */
    public TPMS_PCR_SELECTION() {};
    /**
    * the hash algorithm associated with the selection
    */
    public TPM_ALG_ID hash;
    /**
    * the size in octets of the pcrSelect array
    */
    // private byte sizeofSelect;
    /**
    * the bit map of selected PCR
    */
    public byte[] pcrSelect;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        hash.toTpm(buf);
        buf.writeInt((pcrSelect!=null)?pcrSelect.length:0, 1);
        if(pcrSelect!=null)
            buf.write(pcrSelect);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        hash = TPM_ALG_ID.fromTpm(buf);
        int _sizeofSelect = buf.readInt(1);
        pcrSelect = new byte[_sizeofSelect];
        buf.readArrayOfInts(pcrSelect, 1, _sizeofSelect);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_PCR_SELECTION fromTpm (byte[] x) 
    {
        TPMS_PCR_SELECTION ret = new TPMS_PCR_SELECTION();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_PCR_SELECTION fromTpm (InByteBuf buf) 
    {
        TPMS_PCR_SELECTION ret = new TPMS_PCR_SELECTION();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_PCR_SELECTION");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hash", hash);
        _p.add(d, "byte", "pcrSelect", pcrSelect);
    };
    
    /**
    * Create a PCR_SELECTION naming a single PCR
    * 
    * @param pcrAlg The hash algorithm
    * @param pcrIndex The PCR index
    * 
    */
    public TPMS_PCR_SELECTION(TPM_ALG_ID pcrAlg, int pcrIndex)
    {
    	hash = pcrAlg;
       	int sz = 3;
        if ((pcrIndex / 8 + 1) > sz) {
            sz = pcrIndex  / 8 + 1;
        }
    	pcrSelect = new byte[sz];
        pcrSelect[pcrIndex / 8] = (byte) (1 << (pcrIndex % 8));
    }
    
    /**
    * Create a PCR_SELECTION[] from a single PCR
    * 
    * @param pcrAlg The hash algorithm
    * @param pcrIndex The PCR index
    * @return A new selection array
    */
    public static TPMS_PCR_SELECTION[] CreateSelectionArray(TPM_ALG_ID pcrAlg, int pcrIndex)
    {
    	TPMS_PCR_SELECTION[] arr = new TPMS_PCR_SELECTION[1];
    	arr[0] = new TPMS_PCR_SELECTION(pcrAlg, pcrIndex);
    	return arr;
    }
    
    /**
    * Create a PCR_SELECTION from an array of PCRs in the same bank
    * 
    * @param pcrAlg The hash algorithm
    * @param pcrIndices The PCRs to select
    */
    public TPMS_PCR_SELECTION(TPM_ALG_ID pcrAlg, int[] pcrIndices)
    {
     	hash = pcrAlg;
        int pcrMax = 0;
    
        for (int j = 0; j < pcrIndices.length; j++)
        {
        	if (pcrIndices[j] > pcrMax) pcrMax = pcrIndices[j];
    	}
    
        if (pcrMax < 23) {
            pcrMax = 23;
        }
    
        pcrSelect = new byte[pcrMax / 8 + 1];
    
        for (int j = 0; j < pcrIndices.length; j++) 
        {
            pcrSelect[pcrIndices[j] / 8] |= (byte) (1 << (pcrIndices[j] % 8));
        }
    
        return;
    }
    
    
    
};

//<<<

