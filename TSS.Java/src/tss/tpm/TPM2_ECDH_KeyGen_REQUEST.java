package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe [de]G).
 *  It uses the private ephemeral key and a loaded public key (QS) to compute the shared
 *  secret value (P [hde]QS).
 */
public class TPM2_ECDH_KeyGen_REQUEST extends TpmStructure
{
    /** Handle of a loaded ECC key public area.
     *  Auth Index: None
     */
    public TPM_HANDLE keyHandle;
    
    public TPM2_ECDH_KeyGen_REQUEST() { keyHandle = new TPM_HANDLE(); }
    
    /** @param _keyHandle Handle of a loaded ECC key public area.
     *         Auth Index: None
     */
    public TPM2_ECDH_KeyGen_REQUEST(TPM_HANDLE _keyHandle) { keyHandle = _keyHandle; }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_ECDH_KeyGen_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ECDH_KeyGen_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ECDH_KeyGen_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_ECDH_KeyGen_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ECDH_KeyGen_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECDH_KeyGen_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
    }
}

//<<<
