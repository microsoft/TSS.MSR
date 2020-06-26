package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command changes the authValue of a PCR or group of PCR.  */
public class TPM2_PCR_SetAuthValue_REQUEST extends TpmStructure
{
    /** Handle for a PCR that may have an authorization value set
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE pcrHandle;
    
    /** The desired authorization value  */
    public byte[] auth;
    
    public TPM2_PCR_SetAuthValue_REQUEST() { pcrHandle = new TPM_HANDLE(); }
    
    /** @param _pcrHandle Handle for a PCR that may have an authorization value set
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _auth The desired authorization value
     */
    public TPM2_PCR_SetAuthValue_REQUEST(TPM_HANDLE _pcrHandle, byte[] _auth)
    {
        pcrHandle = _pcrHandle;
        auth = _auth;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(auth); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { auth = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PCR_SetAuthValue_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PCR_SetAuthValue_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PCR_SetAuthValue_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PCR_SetAuthValue_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PCR_SetAuthValue_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_SetAuthValue_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "pcrHandle", pcrHandle);
        _p.add(d, "byte", "auth", auth);
    }
}

//<<<
