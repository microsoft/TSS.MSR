package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command uses the TPM to recover the Z value from a public point (QB) and a
 *  private key (ds). It will perform the multiplication of the provided inPoint (QB) with
 *  the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ)
 *  [hds]QB; where h is the cofactor of the curve).
 */
public class ECDH_ZGenResponse extends RespStructure
{
    /** X and Y coordinates of the product of the multiplication Z = (xZ , yZ) [hdS]QB */
    public TPMS_ECC_POINT outPoint;

    public ECDH_ZGenResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(outPoint); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { outPoint = buf.createSizedObj(TPMS_ECC_POINT.class); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static ECDH_ZGenResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ECDH_ZGenResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static ECDH_ZGenResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static ECDH_ZGenResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ECDH_ZGenResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("ECDH_ZGenResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ECC_POINT", "outPoint", outPoint);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
