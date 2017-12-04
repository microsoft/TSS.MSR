package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to allow a policy to be bound to a specific command and command parameters.
*/
public class TPM2_PolicyCpHash_REQUEST extends TpmStructure
{
    /**
     * This command is used to allow a policy to be bound to a specific command and command parameters.
     * 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _cpHashA the cpHash added to the policy
     */
    public TPM2_PolicyCpHash_REQUEST(TPM_HANDLE _policySession,byte[] _cpHashA)
    {
        policySession = _policySession;
        cpHashA = _cpHashA;
    }
    /**
    * This command is used to allow a policy to be bound to a specific command and command parameters.
    */
    public TPM2_PolicyCpHash_REQUEST() {};
    /**
    * handle for the policy session being extended Auth Index: None
    */
    public TPM_HANDLE policySession;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short cpHashASize;
    /**
    * the cpHash added to the policy
    */
    public byte[] cpHashA;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        policySession.toTpm(buf);
        buf.writeInt((cpHashA!=null)?cpHashA.length:0, 2);
        if(cpHashA!=null)
            buf.write(cpHashA);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        policySession = TPM_HANDLE.fromTpm(buf);
        int _cpHashASize = buf.readInt(2);
        cpHashA = new byte[_cpHashASize];
        buf.readArrayOfInts(cpHashA, 1, _cpHashASize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicyCpHash_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicyCpHash_REQUEST ret = new TPM2_PolicyCpHash_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicyCpHash_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicyCpHash_REQUEST ret = new TPM2_PolicyCpHash_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyCpHash_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "cpHashA", cpHashA);
    };
    
    
};

//<<<

