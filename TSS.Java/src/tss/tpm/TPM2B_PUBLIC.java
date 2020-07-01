package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This sized buffer is used to embed a TPMT_PUBLIC in a load command and in any response
 *  that returns a public area.
 */
public class TPM2B_PUBLIC extends TpmStructure
{
    /** The public area
     *  NOTE The + indicates that the caller may specify that use of TPM_ALG_NULL is allowed
     *  for nameAlg.
     */
    public TPMT_PUBLIC publicArea;
    
    public TPM2B_PUBLIC() {}
    
    /** @param _publicArea The public area
     *         NOTE The + indicates that the caller may specify that use of TPM_ALG_NULL is
     *         allowed for nameAlg.
     */
    public TPM2B_PUBLIC(TPMT_PUBLIC _publicArea) { publicArea = _publicArea; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(publicArea); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { publicArea = buf.createSizedObj(TPMT_PUBLIC.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_PUBLIC fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_PUBLIC.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_PUBLIC fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_PUBLIC fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_PUBLIC.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_PUBLIC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_PUBLIC", "publicArea", publicArea);
    }
}

//<<<
