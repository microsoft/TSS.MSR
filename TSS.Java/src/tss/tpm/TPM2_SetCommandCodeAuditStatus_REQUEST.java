package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command may be used by the Privacy Administrator or platform to change the audit
 *  status of a command or to set the hash algorithm used for the audit digest, but not
 *  both at the same time.
 */
public class TPM2_SetCommandCodeAuditStatus_REQUEST extends ReqStructure
{
    /** TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE auth;
    
    /** Hash algorithm for the audit digest; if TPM_ALG_NULL, then the hash is not changed  */
    public TPM_ALG_ID auditAlg;
    
    /** List of commands that will be added to those that will be audited  */
    public TPM_CC[] setList;
    
    /** List of commands that will no longer be audited  */
    public TPM_CC[] clearList;
    
    public TPM2_SetCommandCodeAuditStatus_REQUEST()
    {
        auth = new TPM_HANDLE();
        auditAlg = TPM_ALG_ID.NULL;
    }
    
    /** @param _auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _auditAlg Hash algorithm for the audit digest; if TPM_ALG_NULL, then the hash is
     *         not changed
     *  @param _setList List of commands that will be added to those that will be audited
     *  @param _clearList List of commands that will no longer be audited
     */
    public TPM2_SetCommandCodeAuditStatus_REQUEST(TPM_HANDLE _auth, TPM_ALG_ID _auditAlg, TPM_CC[] _setList, TPM_CC[] _clearList)
    {
        auth = _auth;
        auditAlg = _auditAlg;
        setList = _setList;
        clearList = _clearList;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        auditAlg.toTpm(buf);
        buf.writeObjArr(setList);
        buf.writeObjArr(clearList);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        auditAlg = TPM_ALG_ID.fromTpm(buf);
        setList = buf.readObjArr(TPM_CC.class);
        clearList = buf.readObjArr(TPM_CC.class);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_SetCommandCodeAuditStatus_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_SetCommandCodeAuditStatus_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_SetCommandCodeAuditStatus_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_SetCommandCodeAuditStatus_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_SetCommandCodeAuditStatus_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SetCommandCodeAuditStatus_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "TPM_ALG_ID", "auditAlg", auditAlg);
        _p.add(d, "TPM_CC[]", "setList", setList);
        _p.add(d, "TPM_CC[]", "clearList", clearList);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {auth}; }
}

//<<<
