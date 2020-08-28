package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 94 Definition of TPMS_PCR_SELECTION Structure  */
public class TPMS_PCR_SELECTION extends TpmStructure
{
    /** The hash algorithm associated with the selection  */
    public TPM_ALG_ID hash;

    /** The bit map of selected PCR  */
    public byte[] pcrSelect;

    public TPMS_PCR_SELECTION() { hash = TPM_ALG_ID.NULL; }

    /** @param _hash The hash algorithm associated with the selection
     *  @param _pcrSelect The bit map of selected PCR
     */
    public TPMS_PCR_SELECTION(TPM_ALG_ID _hash, byte[] _pcrSelect)
    {
        hash = _hash;
        pcrSelect = _pcrSelect;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        hash.toTpm(buf);
        buf.writeSizedByteBuf(pcrSelect, 1);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        hash = TPM_ALG_ID.fromTpm(buf);
        pcrSelect = buf.readSizedByteBuf(1);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_PCR_SELECTION fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_PCR_SELECTION.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_PCR_SELECTION fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_PCR_SELECTION fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_PCR_SELECTION.class);
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
        _p.add(d, "byte[]", "pcrSelect", pcrSelect);
    }

    /** Create a PCR_SELECTION naming a single PCR
     * @param pcrAlg The hash algorithm
     * @param pcrIndex The PCR index
     */
    public TPMS_PCR_SELECTION(TPM_ALG_ID pcrAlg, int pcrIndex)
    {
        hash = pcrAlg;
        int sz = 3;
        if ((pcrIndex / 8 + 1) > sz)
            sz = pcrIndex  / 8 + 1;

        pcrSelect = new byte[sz];
        pcrSelect[pcrIndex / 8] = (byte) (1 << (pcrIndex % 8));
    }

    /** Create a PCR_SELECTION[] from a single PCR
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
            if (pcrIndices[j] > pcrMax)
                pcrMax = pcrIndices[j];
        }

        if (pcrMax < 23)
            pcrMax = 23;

        pcrSelect = new byte[pcrMax / 8 + 1];

        for (int j = 0; j < pcrIndices.length; j++) 
        {
            pcrSelect[pcrIndices[j] / 8] |= (byte)(1 << (pcrIndices[j] % 8));
        }
    }
}

//<<<
