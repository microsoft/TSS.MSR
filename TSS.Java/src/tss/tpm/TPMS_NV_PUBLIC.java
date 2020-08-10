package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure describes an NV Index.  */
public class TPMS_NV_PUBLIC extends TpmStructure
{
    /** The handle of the data area  */
    public TPM_HANDLE nvIndex;
    
    /** Hash algorithm used to compute the name of the Index and used for the authPolicy. For
     *  an extend index, the hash algorithm used for the extend.
     */
    public TPM_ALG_ID nameAlg;
    
    /** The Index attributes  */
    public TPMA_NV attributes;
    
    /** Optional access policy for the Index
     *  The policy is computed using the nameAlg
     *  NOTE Shall be the Empty Policy if no authorization policy is present.
     */
    public byte[] authPolicy;
    
    /** The size of the data area
     *  The maximum size is implementation-dependent. The minimum maximum size is platform-specific.
     */
    public int dataSize;
    
    public TPMS_NV_PUBLIC()
    {
        nvIndex = new TPM_HANDLE();
        nameAlg = TPM_ALG_ID.NULL;
    }
    
    /** @param _nvIndex The handle of the data area
     *  @param _nameAlg Hash algorithm used to compute the name of the Index and used for the
     *         authPolicy. For an extend index, the hash algorithm used for the extend.
     *  @param _attributes The Index attributes
     *  @param _authPolicy Optional access policy for the Index
     *         The policy is computed using the nameAlg
     *         NOTE Shall be the Empty Policy if no authorization policy is present.
     *  @param _dataSize The size of the data area
     *         The maximum size is implementation-dependent. The minimum maximum size is
     *  platform-specific.
     */
    public TPMS_NV_PUBLIC(TPM_HANDLE _nvIndex, TPM_ALG_ID _nameAlg, TPMA_NV _attributes, byte[] _authPolicy, int _dataSize)
    {
        nvIndex = _nvIndex;
        nameAlg = _nameAlg;
        attributes = _attributes;
        authPolicy = _authPolicy;
        dataSize = _dataSize;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        nvIndex.toTpm(buf);
        nameAlg.toTpm(buf);
        attributes.toTpm(buf);
        buf.writeSizedByteBuf(authPolicy);
        buf.writeShort(dataSize);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        nvIndex = TPM_HANDLE.fromTpm(buf);
        nameAlg = TPM_ALG_ID.fromTpm(buf);
        attributes = TPMA_NV.fromTpm(buf);
        authPolicy = buf.readSizedByteBuf();
        dataSize = buf.readShort();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_NV_PUBLIC fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_NV_PUBLIC.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_NV_PUBLIC fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_NV_PUBLIC fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_NV_PUBLIC.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NV_PUBLIC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "TPM_ALG_ID", "nameAlg", nameAlg);
        _p.add(d, "TPMA_NV", "attributes", attributes);
        _p.add(d, "byte[]", "authPolicy", authPolicy);
        _p.add(d, "int", "dataSize", dataSize);
    }
}

//<<<
