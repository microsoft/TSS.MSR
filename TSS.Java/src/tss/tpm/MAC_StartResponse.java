package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command starts a MAC sequence. The TPM will create and initialize an MAC sequence structure, assign a handle to the sequence, and set the authValue of the sequence object to the value in auth.
*/
public class MAC_StartResponse extends TpmStructure
{
    /**
     * This command starts a MAC sequence. The TPM will create and initialize an MAC sequence structure, assign a handle to the sequence, and set the authValue of the sequence object to the value in auth.
     * 
     * @param _handle a handle to reference the sequence
     */
    public MAC_StartResponse(TPM_HANDLE _handle)
    {
        handle = _handle;
    }
    /**
    * This command starts a MAC sequence. The TPM will create and initialize an MAC sequence structure, assign a handle to the sequence, and set the authValue of the sequence object to the value in auth.
    */
    public MAC_StartResponse() {};
    /**
    * a handle to reference the sequence
    */
    public TPM_HANDLE handle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static MAC_StartResponse fromTpm (byte[] x) 
    {
        MAC_StartResponse ret = new MAC_StartResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static MAC_StartResponse fromTpm (InByteBuf buf) 
    {
        MAC_StartResponse ret = new MAC_StartResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_MAC_Start_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
    };
    
    
};

//<<<

