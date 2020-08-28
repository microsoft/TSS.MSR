package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command will take the actual field upgrade image to be installed on the TPM. The
 *  exact format of fuData is vendor-specific. This command is only possible following a
 *  successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized
 *  TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
 */
public class TPM2_FieldUpgradeData_REQUEST extends ReqStructure
{
    /** Field upgrade image data  */
    public byte[] fuData;

    public TPM2_FieldUpgradeData_REQUEST() {}

    /** @param _fuData Field upgrade image data  */
    public TPM2_FieldUpgradeData_REQUEST(byte[] _fuData) { fuData = _fuData; }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(fuData); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { fuData = buf.readSizedByteBuf(); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_FieldUpgradeData_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_FieldUpgradeData_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_FieldUpgradeData_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_FieldUpgradeData_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_FieldUpgradeData_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FieldUpgradeData_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "fuData", fuData);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
