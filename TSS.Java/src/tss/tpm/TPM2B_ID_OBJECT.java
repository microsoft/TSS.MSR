package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is an output from TPM2_MakeCredential() and is an input to
 *  TPM2_ActivateCredential().
 */
public class TPM2B_ID_OBJECT extends TpmStructure
{
    /** An encrypted credential area  */
    public TPMS_ID_OBJECT credential;
    
    public TPM2B_ID_OBJECT() {}
    
    /** @param _credential An encrypted credential area  */
    public TPM2B_ID_OBJECT(TPMS_ID_OBJECT _credential) { credential = _credential; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(credential); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { credential = buf.createSizedObj(TPMS_ID_OBJECT.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_ID_OBJECT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_ID_OBJECT.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_ID_OBJECT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_ID_OBJECT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_ID_OBJECT.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_ID_OBJECT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ID_OBJECT", "credential", credential);
    }
}

//<<<
