package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command includes a signed authorization in a policy. The command ties the policy
 *  to a signing key by including the Name of the signing key in the policyDigest
 */
public class PolicySignedResponse extends RespStructure
{
    /** Implementation-specific time value, used to indicate to the TPM when the ticket expires
     *  NOTE If policyTicket is a NULL Ticket, then this shall be the Empty Buffer.
     */
    public byte[] timeout;
    
    /** Produced if the command succeeds and expiration in the command was non-zero; this
     *  ticket will use the TPMT_ST_AUTH_SIGNED structure tag. See 23.2.5
     */
    public TPMT_TK_AUTH policyTicket;
    
    public PolicySignedResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(timeout);
        policyTicket.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        timeout = buf.readSizedByteBuf();
        policyTicket = TPMT_TK_AUTH.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static PolicySignedResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(PolicySignedResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static PolicySignedResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static PolicySignedResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(PolicySignedResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("PolicySignedResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "timeout", timeout);
        _p.add(d, "TPMT_TK_AUTH", "policyTicket", policyTicket);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<
