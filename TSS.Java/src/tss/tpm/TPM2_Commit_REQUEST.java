package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** TPM2_Commit() performs the first part of an ECC anonymous signing operation. The TPM
 *  will perform the point multiplications on the provided points and return intermediate
 *  signing values. The signHandle parameter shall refer to an ECC key and the signing
 *  scheme must be anonymous (TPM_RC_SCHEME).
 */
public class TPM2_Commit_REQUEST extends ReqStructure
{
    /** Handle of the key that will be used in the signing operation
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;
    
    /** A point (M) on the curve used by signHandle  */
    public TPMS_ECC_POINT P1;
    
    /** Octet array used to derive x-coordinate of a base point  */
    public byte[] s2;
    
    /** Y coordinate of the point associated with s2  */
    public byte[] y2;
    
    public TPM2_Commit_REQUEST() { signHandle = new TPM_HANDLE(); }
    
    /** @param _signHandle Handle of the key that will be used in the signing operation
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _P1 A point (M) on the curve used by signHandle
     *  @param _s2 Octet array used to derive x-coordinate of a base point
     *  @param _y2 Y coordinate of the point associated with s2
     */
    public TPM2_Commit_REQUEST(TPM_HANDLE _signHandle, TPMS_ECC_POINT _P1, byte[] _s2, byte[] _y2)
    {
        signHandle = _signHandle;
        P1 = _P1;
        s2 = _s2;
        y2 = _y2;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(P1);
        buf.writeSizedByteBuf(s2);
        buf.writeSizedByteBuf(y2);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        P1 = buf.createSizedObj(TPMS_ECC_POINT.class);
        s2 = buf.readSizedByteBuf();
        y2 = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_Commit_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Commit_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_Commit_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_Commit_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Commit_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Commit_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "TPMS_ECC_POINT", "P1", P1);
        _p.add(d, "byte", "s2", s2);
        _p.add(d, "byte", "y2", y2);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {signHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
