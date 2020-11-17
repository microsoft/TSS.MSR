package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command includes a secret-based authorization to a policy. The caller proves
 *  knowledge of the secret value using an authorization session using the authValue
 *  associated with authHandle. A password session, an HMAC session, or a policy session
 *  containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
 */
public class PolicySecretResponse extends RespStructure
{
    /** Implementation-specific time value used to indicate to the TPM when the ticket expires */
    public byte[] timeout;

    /** Produced if the command succeeds and expiration in the command was non-zero ( See
     *  23.2.5). This ticket will use the TPMT_ST_AUTH_SECRET structure tag
     */
    public TPMT_TK_AUTH policyTicket;

    public PolicySecretResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(timeout);
        policyTicket.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        timeout = buf.readSizedByteBuf();
        policyTicket = TPMT_TK_AUTH.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static PolicySecretResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(PolicySecretResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static PolicySecretResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static PolicySecretResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(PolicySecretResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("PolicySecretResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "timeout", timeout);
        _p.add(d, "TPMT_TK_AUTH", "policyTicket", policyTicket);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
