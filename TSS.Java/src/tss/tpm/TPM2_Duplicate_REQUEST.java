package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command duplicates a loaded object so that it may be used in a different
 *  hierarchy. The new parent key for the duplicate may be on the same or different TPM or
 *  TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
 */
public class TPM2_Duplicate_REQUEST extends TpmStructure
{
    /** Loaded object to duplicate
     *  Auth Index: 1
     *  Auth Role: DUP
     */
    public TPM_HANDLE objectHandle;
    
    /** Shall reference the public area of an asymmetric key
     *  Auth Index: None
     */
    public TPM_HANDLE newParentHandle;
    
    /** Optional symmetric encryption key
     *  The size for this key is set to zero when the TPM is to generate the key. This
     *  parameter may be encrypted.
     */
    public byte[] encryptionKeyIn;
    
    /** Definition for the symmetric algorithm to be used for the inner wrapper
     *  may be TPM_ALG_NULL if no inner wrapper is applied
     */
    public TPMT_SYM_DEF_OBJECT symmetricAlg;
    
    public TPM2_Duplicate_REQUEST()
    {
        objectHandle = new TPM_HANDLE();
        newParentHandle = new TPM_HANDLE();
    }
    
    /** @param _objectHandle Loaded object to duplicate
     *         Auth Index: 1
     *         Auth Role: DUP
     *  @param _newParentHandle Shall reference the public area of an asymmetric key
     *         Auth Index: None
     *  @param _encryptionKeyIn Optional symmetric encryption key
     *         The size for this key is set to zero when the TPM is to generate the key. This
     *         parameter may be encrypted.
     *  @param _symmetricAlg Definition for the symmetric algorithm to be used for the inner wrapper
     *         may be TPM_ALG_NULL if no inner wrapper is applied
     */
    public TPM2_Duplicate_REQUEST(TPM_HANDLE _objectHandle, TPM_HANDLE _newParentHandle, byte[] _encryptionKeyIn, TPMT_SYM_DEF_OBJECT _symmetricAlg)
    {
        objectHandle = _objectHandle;
        newParentHandle = _newParentHandle;
        encryptionKeyIn = _encryptionKeyIn;
        symmetricAlg = _symmetricAlg;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(encryptionKeyIn);
        symmetricAlg.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        encryptionKeyIn = buf.readSizedByteBuf();
        symmetricAlg = TPMT_SYM_DEF_OBJECT.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_Duplicate_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Duplicate_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Duplicate_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_Duplicate_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Duplicate_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Duplicate_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "TPM_HANDLE", "newParentHandle", newParentHandle);
        _p.add(d, "byte", "encryptionKeyIn", encryptionKeyIn);
        _p.add(d, "TPMT_SYM_DEF_OBJECT", "symmetricAlg", symmetricAlg);
    }
}

//<<<
