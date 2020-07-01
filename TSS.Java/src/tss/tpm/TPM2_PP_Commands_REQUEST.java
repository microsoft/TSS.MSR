package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to determine which commands require assertion of Physical
 *  Presence (PP) in addition to platformAuth/platformPolicy.
 */
public class TPM2_PP_Commands_REQUEST extends ReqStructure
{
    /** TPM_RH_PLATFORM+PP
     *  Auth Index: 1
     *  Auth Role: USER + Physical Presence
     */
    public TPM_HANDLE auth;
    
    /** List of commands to be added to those that will require that Physical Presence be asserted  */
    public TPM_CC[] setList;
    
    /** List of commands that will no longer require that Physical Presence be asserted  */
    public TPM_CC[] clearList;
    
    public TPM2_PP_Commands_REQUEST() { auth = new TPM_HANDLE(); }
    
    /** @param _auth TPM_RH_PLATFORM+PP
     *         Auth Index: 1
     *         Auth Role: USER + Physical Presence
     *  @param _setList List of commands to be added to those that will require that Physical
     *         Presence be asserted
     *  @param _clearList List of commands that will no longer require that Physical Presence
     *  be asserted
     */
    public TPM2_PP_Commands_REQUEST(TPM_HANDLE _auth, TPM_CC[] _setList, TPM_CC[] _clearList)
    {
        auth = _auth;
        setList = _setList;
        clearList = _clearList;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeObjArr(setList);
        buf.writeObjArr(clearList);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        setList = buf.readObjArr(TPM_CC.class);
        clearList = buf.readObjArr(TPM_CC.class);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PP_Commands_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PP_Commands_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PP_Commands_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PP_Commands_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PP_Commands_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PP_Commands_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "TPM_CC", "setList", setList);
        _p.add(d, "TPM_CC", "clearList", clearList);
    }

    @Override
    public int numHandles() { return 1; }
    
    public int numAuthHandles() { return 1; }
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {auth}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(4, 4); }
}

//<<<
