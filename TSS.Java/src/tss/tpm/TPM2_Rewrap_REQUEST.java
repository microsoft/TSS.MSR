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
public class TPM2_Rewrap_REQUEST extends TpmStructure
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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        inDuplicate.toTpm(buf);
        buf.writeSizedByteBuf(name);
        buf.writeSizedByteBuf(inSymSeed);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        inDuplicate = TPM2B_PRIVATE.fromTpm(buf);
        int _nameSize = buf.readShort() & 0xFFFF;
        name = new byte[_nameSize];
        buf.readArrayOfInts(name, 1, _nameSize);
        int _inSymSeedSize = buf.readShort() & 0xFFFF;
        inSymSeed = new byte[_inSymSeedSize];
        buf.readArrayOfInts(inSymSeed, 1, _inSymSeedSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_Rewrap_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_Rewrap_REQUEST ret = new TPM2_Rewrap_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Rewrap_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_Rewrap_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Rewrap_REQUEST ret = new TPM2_Rewrap_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
}

//<<<
