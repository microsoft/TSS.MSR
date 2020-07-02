package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to start an authorization session using alternative methods of
 *  establishing the session key (sessionKey). The session key is then used to derive
 *  values used for authorization and for encrypting parameters.
 */
public class StartAuthSessionResponse extends RespStructure
{
    /** Handle for the newly created session  */
    public TPM_HANDLE handle;
    
    /** The initial nonce from the TPM, used in the computation of the sessionKey  */
    public byte[] nonceTPM;
    
    public StartAuthSessionResponse() { handle = new TPM_HANDLE(); }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(nonceTPM); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { nonceTPM = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static StartAuthSessionResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(StartAuthSessionResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static StartAuthSessionResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static StartAuthSessionResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(StartAuthSessionResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_StartAuthSession_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte", "nonceTPM", nonceTPM);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public TPM_HANDLE getHandle() { return handle; }

    @Override
    public void setHandle(TPM_HANDLE h) { handle = h; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
