package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command includes a signed authorization in a policy. The command ties the policy to a signing key by including the Name of the signing key in the policyDigest
*/
public class PolicySignedResponse extends TpmStructure
{
    /**
     * This command includes a signed authorization in a policy. The command ties the policy to a signing key by including the Name of the signing key in the policyDigest
     * 
     * @param _timeout implementation-specific time value, used to indicate to the TPM when the ticket expires NOTE If policyTicket is a NULL Ticket, then this shall be the Empty Buffer. 
     * @param _policyTicket produced if the command succeeds and expiration in the command was non-zero; this ticket will use the TPMT_ST_AUTH_SIGNED structure tag. See 23.2.5
     */
    public PolicySignedResponse(byte[] _timeout,TPMT_TK_AUTH _policyTicket)
    {
        timeout = _timeout;
        policyTicket = _policyTicket;
    }
    /**
    * This command includes a signed authorization in a policy. The command ties the policy to a signing key by including the Name of the signing key in the policyDigest
    */
    public PolicySignedResponse() {};
    /**
    * size of the timeout value
    */
    // private short timeoutSize;
    /**
    * implementation-specific time value, used to indicate to the TPM when the ticket expires NOTE If policyTicket is a NULL Ticket, then this shall be the Empty Buffer.
    */
    public byte[] timeout;
    /**
    * produced if the command succeeds and expiration in the command was non-zero; this ticket will use the TPMT_ST_AUTH_SIGNED structure tag. See 23.2.5
    */
    public TPMT_TK_AUTH policyTicket;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((timeout!=null)?timeout.length:0, 2);
        if(timeout!=null)
            buf.write(timeout);
        policyTicket.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _timeoutSize = buf.readInt(2);
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
    public static PolicySignedResponse fromTpm (byte[] x) 
    {
        PolicySignedResponse ret = new PolicySignedResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PolicySignedResponse fromTpm (InByteBuf buf) 
    {
        PolicySignedResponse ret = new PolicySignedResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicySigned_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "timeout", timeout);
        _p.add(d, "TPMT_TK_AUTH", "policyTicket", policyTicket);
    };
    
    
};

//<<<

