package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe [de]G).
 *  It uses the private ephemeral key and a loaded public key (QS) to compute the shared
 *  secret value (P [hde]QS).
 */
public class ECDH_KeyGenResponse extends RespStructure
{
    /** Results of P h[de]Qs */
    public TPMS_ECC_POINT zPoint;

    /** Generated ephemeral public point (Qe) */
    public TPMS_ECC_POINT pubPoint;

    public ECDH_KeyGenResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(zPoint);
        buf.writeSizedObj(pubPoint);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        zPoint = buf.createSizedObj(TPMS_ECC_POINT.class);
        pubPoint = buf.createSizedObj(TPMS_ECC_POINT.class);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static ECDH_KeyGenResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ECDH_KeyGenResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static ECDH_KeyGenResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static ECDH_KeyGenResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ECDH_KeyGenResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("ECDH_KeyGenResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "zPoint", zPoint);
        _p.add(d, "TPMS_ECC_POINT", "pubPoint", pubPoint);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
