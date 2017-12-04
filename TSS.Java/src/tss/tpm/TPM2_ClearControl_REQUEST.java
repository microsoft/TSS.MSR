package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* TPM2_ClearControl() disables and enables the execution of TPM2_Clear().
*/
public class TPM2_ClearControl_REQUEST extends TpmStructure
{
    /**
     * TPM2_ClearControl() disables and enables the execution of TPM2_Clear().
     * 
     * @param _auth TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER 
     * @param _disable YES if the disableOwnerClear flag is to be SET, NO if the flag is to be CLEAR.
     */
    public TPM2_ClearControl_REQUEST(TPM_HANDLE _auth,byte _disable)
    {
        auth = _auth;
        disable = _disable;
    }
    /**
    * TPM2_ClearControl() disables and enables the execution of TPM2_Clear().
    */
    public TPM2_ClearControl_REQUEST() {};
    /**
    * TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER
    */
    public TPM_HANDLE auth;
    /**
    * YES if the disableOwnerClear flag is to be SET, NO if the flag is to be CLEAR.
    */
    public byte disable;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        auth.toTpm(buf);
        buf.write(disable);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        auth = TPM_HANDLE.fromTpm(buf);
        disable = (byte) buf.readInt(1);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ClearControl_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ClearControl_REQUEST ret = new TPM2_ClearControl_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ClearControl_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ClearControl_REQUEST ret = new TPM2_ClearControl_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ClearControl_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "BYTE", "disable", disable);
    };
    
    
};

//<<<

