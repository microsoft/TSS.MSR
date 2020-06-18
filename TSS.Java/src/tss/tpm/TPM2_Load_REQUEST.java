package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to load objects into the TPM. This command is used when both a
 *  TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be
 *  loaded, the TPM2_LoadExternal command is used.
 */
public class TPM2_Load_REQUEST extends TpmStructure
{
    /** TPM handle of parent key; shall not be a reserved handle
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE parentHandle;
    
    /** The private portion of the object  */
    public TPM2B_PRIVATE inPrivate;
    
    /** The public portion of the object  */
    public TPMT_PUBLIC inPublic;
    
    public TPM2_Load_REQUEST() { parentHandle = new TPM_HANDLE(); }
    
    /** @param _parentHandle TPM handle of parent key; shall not be a reserved handle
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _inPrivate The private portion of the object
     *  @param _inPublic The public portion of the object
     */
    public TPM2_Load_REQUEST(TPM_HANDLE _parentHandle, TPM2B_PRIVATE _inPrivate, TPMT_PUBLIC _inPublic)
    {
        parentHandle = _parentHandle;
        inPrivate = _inPrivate;
        inPublic = _inPublic;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        inPrivate.toTpm(buf);
        buf.writeShort(inPublic != null ? inPublic.toTpm().length : 0);
        if (inPublic != null)
            inPublic.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        inPrivate = TPM2B_PRIVATE.fromTpm(buf);
        int _inPublicSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inPublicSize));
        inPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_Load_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_Load_REQUEST ret = new TPM2_Load_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Load_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_Load_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Load_REQUEST ret = new TPM2_Load_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Load_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "parentHandle", parentHandle);
        _p.add(d, "TPM2B_PRIVATE", "inPrivate", inPrivate);
        _p.add(d, "TPMT_PUBLIC", "inPublic", inPublic);
    }
}

//<<<
