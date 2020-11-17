package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure contains the sensitive creation data in a sized buffer. This structure
 *  is defined so that both the userAuth and data values of the TPMS_SENSITIVE_CREATE may
 *  be passed as a single parameter for parameter encryption purposes.
 */
public class TPM2B_SENSITIVE_CREATE extends TpmStructure
{
    /** Data to be sealed or a symmetric key value. */
    public TPMS_SENSITIVE_CREATE sensitive;

    public TPM2B_SENSITIVE_CREATE() {}

    /** @param _sensitive Data to be sealed or a symmetric key value. */
    public TPM2B_SENSITIVE_CREATE(TPMS_SENSITIVE_CREATE _sensitive) { sensitive = _sensitive; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(sensitive); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { sensitive = buf.createSizedObj(TPMS_SENSITIVE_CREATE.class); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_SENSITIVE_CREATE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_SENSITIVE_CREATE.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_SENSITIVE_CREATE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_SENSITIVE_CREATE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_SENSITIVE_CREATE.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_SENSITIVE_CREATE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_SENSITIVE_CREATE", "sensitive", sensitive);
    }
}

//<<<
