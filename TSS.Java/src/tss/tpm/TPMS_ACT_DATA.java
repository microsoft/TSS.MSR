package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used in TPM2_GetCapability() to return the ACT data.  */
public class TPMS_ACT_DATA extends TpmStructure
{
    /** A permanent handle  */
    public TPM_HANDLE handle;

    /** The current timeout of the ACT  */
    public int timeout;

    /** The state of the ACT  */
    public TPMA_ACT attributes;

    public TPMS_ACT_DATA() { handle = new TPM_HANDLE(); }

    /** @param _handle A permanent handle
     *  @param _timeout The current timeout of the ACT
     *  @param _attributes The state of the ACT
     */
    public TPMS_ACT_DATA(TPM_HANDLE _handle, int _timeout, TPMA_ACT _attributes)
    {
        handle = _handle;
        timeout = _timeout;
        attributes = _attributes;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        handle.toTpm(buf);
        buf.writeInt(timeout);
        attributes.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        timeout = buf.readInt();
        attributes = TPMA_ACT.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_ACT_DATA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_ACT_DATA.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_ACT_DATA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_ACT_DATA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_ACT_DATA.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ACT_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "int", "timeout", timeout);
        _p.add(d, "TPMA_ACT", "attributes", attributes);
    }
}

//<<<
