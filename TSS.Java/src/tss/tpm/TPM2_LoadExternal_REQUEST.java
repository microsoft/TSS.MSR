package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to load an object that is not a Protected Object into the TPM.
 *  The command allows loading of a public area or both a public and sensitive area.
 */
public class TPM2_LoadExternal_REQUEST extends ReqStructure
{
    /** The sensitive portion of the object (optional) */
    public TPMT_SENSITIVE inPrivate;

    /** The public portion of the object */
    public TPMT_PUBLIC inPublic;

    /** Hierarchy with which the object area is associated */
    public TPM_HANDLE hierarchy;

    public TPM2_LoadExternal_REQUEST() { hierarchy = new TPM_HANDLE(); }

    /** @param _inPrivate The sensitive portion of the object (optional)
     *  @param _inPublic The public portion of the object
     *  @param _hierarchy Hierarchy with which the object area is associated
     */
    public TPM2_LoadExternal_REQUEST(TPMT_SENSITIVE _inPrivate, TPMT_PUBLIC _inPublic, TPM_HANDLE _hierarchy)
    {
        inPrivate = _inPrivate;
        inPublic = _inPublic;
        hierarchy = _hierarchy;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(inPrivate);
        buf.writeSizedObj(inPublic);
        hierarchy.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        inPrivate = buf.createSizedObj(TPMT_SENSITIVE.class);
        inPublic = buf.createSizedObj(TPMT_PUBLIC.class);
        hierarchy = TPM_HANDLE.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_LoadExternal_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_LoadExternal_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_LoadExternal_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_LoadExternal_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_LoadExternal_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_LoadExternal_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_SENSITIVE", "inPrivate", inPrivate);
        _p.add(d, "TPMT_PUBLIC", "inPublic", inPublic);
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
