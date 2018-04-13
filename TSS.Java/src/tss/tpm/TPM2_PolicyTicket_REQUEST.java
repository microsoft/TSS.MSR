package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is similar to TPM2_PolicySigned() except that it takes a ticket instead of a signed authorization. The ticket represents a validated authorization that had an expiration time associated with it.
*/
public class TPM2_PolicyTicket_REQUEST extends TpmStructure
{
    /**
     * This command is similar to TPM2_PolicySigned() except that it takes a ticket instead of a signed authorization. The ticket represents a validated authorization that had an expiration time associated with it.
     * 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _timeout time when authorization will expire The contents are TPM specific. This shall be the value returned when ticket was produced. 
     * @param _cpHashA digest of the command parameters to which this authorization is limited If it is not limited, the parameter will be the Empty Buffer. 
     * @param _policyRef reference to a qualifier for the policy may be the Empty Buffer 
     * @param _authName name of the object that provided the authorization 
     * @param _ticket an authorization ticket returned by the TPM in response to a TPM2_PolicySigned() or TPM2_PolicySecret()
     */
    public TPM2_PolicyTicket_REQUEST(TPM_HANDLE _policySession,byte[] _timeout,byte[] _cpHashA,byte[] _policyRef,byte[] _authName,TPMT_TK_AUTH _ticket)
    {
        policySession = _policySession;
        timeout = _timeout;
        cpHashA = _cpHashA;
        policyRef = _policyRef;
        authName = _authName;
        ticket = _ticket;
    }
    /**
    * This command is similar to TPM2_PolicySigned() except that it takes a ticket instead of a signed authorization. The ticket represents a validated authorization that had an expiration time associated with it.
    */
    public TPM2_PolicyTicket_REQUEST() {};
    /**
    * handle for the policy session being extended Auth Index: None
    */
    public TPM_HANDLE policySession;
    /**
    * size of the timeout value
    */
    // private short timeoutSize;
    /**
    * time when authorization will expire The contents are TPM specific. This shall be the value returned when ticket was produced.
    */
    public byte[] timeout;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short cpHashASize;
    /**
    * digest of the command parameters to which this authorization is limited If it is not limited, the parameter will be the Empty Buffer.
    */
    public byte[] cpHashA;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short policyRefSize;
    /**
    * reference to a qualifier for the policy may be the Empty Buffer
    */
    public byte[] policyRef;
    /**
    * size of the Name structure
    */
    // private short authNameSize;
    /**
    * name of the object that provided the authorization
    */
    public byte[] authName;
    /**
    * an authorization ticket returned by the TPM in response to a TPM2_PolicySigned() or TPM2_PolicySecret()
    */
    public TPMT_TK_AUTH ticket;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        buf.writeInt((timeout!=null)?timeout.length:0, 2);
        if(timeout!=null)
            buf.write(timeout);
        buf.writeInt((cpHashA!=null)?cpHashA.length:0, 2);
        if(cpHashA!=null)
            buf.write(cpHashA);
        buf.writeInt((policyRef!=null)?policyRef.length:0, 2);
        if(policyRef!=null)
            buf.write(policyRef);
        buf.writeInt((authName!=null)?authName.length:0, 2);
        if(authName!=null)
            buf.write(authName);
        ticket.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        int _timeoutSize = buf.readInt(2);
        timeout = new byte[_timeoutSize];
        buf.readArrayOfInts(timeout, 1, _timeoutSize);
        int _cpHashASize = buf.readInt(2);
        cpHashA = new byte[_cpHashASize];
        buf.readArrayOfInts(cpHashA, 1, _cpHashASize);
        int _policyRefSize = buf.readInt(2);
        policyRef = new byte[_policyRefSize];
        buf.readArrayOfInts(policyRef, 1, _policyRefSize);
        int _authNameSize = buf.readInt(2);
        authName = new byte[_authNameSize];
        buf.readArrayOfInts(authName, 1, _authNameSize);
        ticket = TPMT_TK_AUTH.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyTicket_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyTicket_REQUEST ret = new TPM2_PolicyTicket_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyTicket_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyTicket_REQUEST ret = new TPM2_PolicyTicket_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyTicket_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "timeout", timeout);
        _p.add(d, "byte", "cpHashA", cpHashA);
        _p.add(d, "byte", "policyRef", policyRef);
        _p.add(d, "byte", "authName", authName);
        _p.add(d, "TPMT_TK_AUTH", "ticket", ticket);
    };
    
    
};

//<<<

