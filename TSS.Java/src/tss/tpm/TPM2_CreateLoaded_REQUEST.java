package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command creates an object and loads it in the TPM. This command allows creation
 *  of any type of object (Primary, Ordinary, or Derived) depending on the type of
 *  parentHandle. If parentHandle references a Primary Seed, then a Primary Object is
 *  created; if parentHandle references a Storage Parent, then an Ordinary Object is
 *  created; and if parentHandle references a Derivation Parent, then a Derived Object is generated.
 */
public class TPM2_CreateLoaded_REQUEST extends ReqStructure
{
    /** Handle of a transient storage key, a persistent storage key, TPM_RH_ENDORSEMENT,
     *  TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE parentHandle;
    
    /** The sensitive data, see TPM 2.0 Part 1 Sensitive Values  */
    public TPMS_SENSITIVE_CREATE inSensitive;
    
    /** The public template  */
    public byte[] inPublic;
    
    public TPM2_CreateLoaded_REQUEST() { parentHandle = new TPM_HANDLE(); }
    
    /** @param _parentHandle Handle of a transient storage key, a persistent storage key,
     *         TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _inSensitive The sensitive data, see TPM 2.0 Part 1 Sensitive Values
     *  @param _inPublic The public template
     */
    public TPM2_CreateLoaded_REQUEST(TPM_HANDLE _parentHandle, TPMS_SENSITIVE_CREATE _inSensitive, byte[] _inPublic)
    {
        parentHandle = _parentHandle;
        inSensitive = _inSensitive;
        inPublic = _inPublic;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(inSensitive);
        buf.writeSizedByteBuf(inPublic);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        inSensitive = buf.createSizedObj(TPMS_SENSITIVE_CREATE.class);
        inPublic = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_CreateLoaded_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_CreateLoaded_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_CreateLoaded_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_CreateLoaded_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_CreateLoaded_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CreateLoaded_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "parentHandle", parentHandle);
        _p.add(d, "TPMS_SENSITIVE_CREATE", "inSensitive", inSensitive);
        _p.add(d, "byte[]", "inPublic", inPublic);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {parentHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
