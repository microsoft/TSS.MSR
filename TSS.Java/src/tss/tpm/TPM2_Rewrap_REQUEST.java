package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows the TPM to serve in the role as a Duplication Authority. If proper
 *  authorization for use of the oldParent is provided, then an HMAC key and a symmetric
 *  key are recovered from inSymSeed and used to integrity check and decrypt inDuplicate.
 *  A new protection seed value is generated according to the methods appropriate for
 *  newParent and the blob is re-encrypted and a new integrity value is computed. The
 *  re-encrypted blob is returned in outDuplicate and the symmetric key returned in outSymKey.
 */
public class TPM2_Rewrap_REQUEST extends ReqStructure
{
    /** Parent of object
     *  Auth Index: 1
     *  Auth Role: User
     */
    public TPM_HANDLE oldParent;
    
    /** New parent of the object
     *  Auth Index: None
     */
    public TPM_HANDLE newParent;
    
    /** An object encrypted using symmetric key derived from inSymSeed  */
    public TPM2B_PRIVATE inDuplicate;
    
    /** The Name of the object being rewrapped  */
    public byte[] name;
    
    /** The seed for the symmetric key and HMAC key
     *  needs oldParent private key to recover the seed and generate the symmetric key
     */
    public byte[] inSymSeed;
    
    public TPM2_Rewrap_REQUEST()
    {
        oldParent = new TPM_HANDLE();
        newParent = new TPM_HANDLE();
    }
    
    /** @param _oldParent Parent of object
     *         Auth Index: 1
     *         Auth Role: User
     *  @param _newParent New parent of the object
     *         Auth Index: None
     *  @param _inDuplicate An object encrypted using symmetric key derived from inSymSeed
     *  @param _name The Name of the object being rewrapped
     *  @param _inSymSeed The seed for the symmetric key and HMAC key
     *         needs oldParent private key to recover the seed and generate the symmetric key
     */
    public TPM2_Rewrap_REQUEST(TPM_HANDLE _oldParent, TPM_HANDLE _newParent, TPM2B_PRIVATE _inDuplicate, byte[] _name, byte[] _inSymSeed)
    {
        oldParent = _oldParent;
        newParent = _newParent;
        inDuplicate = _inDuplicate;
        name = _name;
        inSymSeed = _inSymSeed;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        inDuplicate.toTpm(buf);
        buf.writeSizedByteBuf(name);
        buf.writeSizedByteBuf(inSymSeed);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        inDuplicate = TPM2B_PRIVATE.fromTpm(buf);
        name = buf.readSizedByteBuf();
        inSymSeed = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_Rewrap_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Rewrap_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Rewrap_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_Rewrap_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Rewrap_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Rewrap_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "oldParent", oldParent);
        _p.add(d, "TPM_HANDLE", "newParent", newParent);
        _p.add(d, "TPM2B_PRIVATE", "inDuplicate", inDuplicate);
        _p.add(d, "byte", "name", name);
        _p.add(d, "byte", "inSymSeed", inSymSeed);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {oldParent, newParent}; }
}

//<<<
