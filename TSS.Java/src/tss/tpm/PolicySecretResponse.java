package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command includes a secret-based authorization to a policy. The caller proves
 *  knowledge of the secret value using an authorization session using the authValue
 *  associated with authHandle. A password session, an HMAC session, or a policy session
 *  containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
 */
public class PolicySecretResponse extends TpmStructure
{
    /** implementation-specific time value used to indicate to the TPM when the ticket expires */
    public byte[] timeout;
    
    /**
     *  produced if the command succeeds and expiration in the command was non-zero ( See 23.2.5).
     *  This ticket will use the TPMT_ST_AUTH_SECRET structure tag
     */
    public TPMT_TK_AUTH policyTicket;
    
    public PolicySecretResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(timeout);
        policyTicket.toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _timeoutSize = buf.readShort() & 0xFFFF;
        timeout = new byte[_timeoutSize];
        buf.readArrayOfInts(timeout, 1, _timeoutSize);
        policyTicket = TPMT_TK_AUTH.fromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static PolicySecretResponse fromTpm (byte[] x) 
    {
        PolicySecretResponse ret = new PolicySecretResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static PolicySecretResponse fromTpm (InByteBuf buf) 
    {
        PolicySecretResponse ret = new PolicySecretResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicySecret_RESPONSE");
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
}

//<<<

