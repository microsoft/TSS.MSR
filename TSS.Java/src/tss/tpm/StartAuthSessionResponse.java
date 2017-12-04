package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to start an authorization session using alternative methods of establishing the session key (sessionKey). The session key is then used to derive values used for authorization and for encrypting parameters.
*/
public class StartAuthSessionResponse extends TpmStructure
{
    /**
     * This command is used to start an authorization session using alternative methods of establishing the session key (sessionKey). The session key is then used to derive values used for authorization and for encrypting parameters.
     * 
     * @param _handle handle for the newly created session 
     * @param _nonceTPM the initial nonce from the TPM, used in the computation of the sessionKey
     */
    public StartAuthSessionResponse(TPM_HANDLE _handle,byte[] _nonceTPM)
    {
        handle = _handle;
        nonceTPM = _nonceTPM;
    }
    /**
    * This command is used to start an authorization session using alternative methods of establishing the session key (sessionKey). The session key is then used to derive values used for authorization and for encrypting parameters.
    */
    public StartAuthSessionResponse() {};
    /**
    * handle for the newly created session
    */
    public TPM_HANDLE handle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short nonceTPMSize;
    /**
    * the initial nonce from the TPM, used in the computation of the sessionKey
    */
    public byte[] nonceTPM;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        buf.writeInt((nonceTPM!=null)?nonceTPM.length:0, 2);
        if(nonceTPM!=null)
            buf.write(nonceTPM);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        int _nonceTPMSize = buf.readInt(2);
        nonceTPM = new byte[_nonceTPMSize];
        buf.readArrayOfInts(nonceTPM, 1, _nonceTPMSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static StartAuthSessionResponse fromTpm (byte[] x) 
    {
        StartAuthSessionResponse ret = new StartAuthSessionResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static StartAuthSessionResponse fromTpm (InByteBuf buf) 
    {
        StartAuthSessionResponse ret = new StartAuthSessionResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_StartAuthSession_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte", "nonceTPM", nonceTPM);
    };
    
    
};

//<<<

