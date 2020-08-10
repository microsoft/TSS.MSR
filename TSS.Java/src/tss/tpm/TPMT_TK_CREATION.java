package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This ticket is produced by TPM2_Create() or TPM2_CreatePrimary(). It is used to bind
 *  the creation data to the object to which it applies. The ticket is computed by
 */
public class TPMT_TK_CREATION extends TpmStructure
{
    /** The hierarchy containing name  */
    public TPM_HANDLE hierarchy;
    
    /** This shall be the HMAC produced using a proof value of hierarchy.  */
    public byte[] digest;
    
    public TPMT_TK_CREATION() { hierarchy = new TPM_HANDLE(); }
    
    /** @param _hierarchy The hierarchy containing name
     *  @param _digest This shall be the HMAC produced using a proof value of hierarchy.
     */
    public TPMT_TK_CREATION(TPM_HANDLE _hierarchy, byte[] _digest)
    {
        hierarchy = _hierarchy;
        digest = _digest;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeShort(TPM_ST.CREATION);
        hierarchy.toTpm(buf);
        buf.writeSizedByteBuf(digest);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        buf.readShort();
        hierarchy = TPM_HANDLE.fromTpm(buf);
        digest = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMT_TK_CREATION fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_TK_CREATION.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_TK_CREATION fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMT_TK_CREATION fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_TK_CREATION.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_TK_CREATION");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
        _p.add(d, "byte[]", "digest", digest);
    }
}

//<<<
