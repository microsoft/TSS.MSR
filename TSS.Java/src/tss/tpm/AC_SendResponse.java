package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* The purpose of this command is to send (copy) a loaded object from the TPM to an Attached Component.
*/
public class AC_SendResponse extends TpmStructure
{
    /**
     * The purpose of this command is to send (copy) a loaded object from the TPM to an Attached Component.
     * 
     * @param _acDataOut May include AC specific data or information about an error.
     */
    public AC_SendResponse(TPMS_AC_OUTPUT _acDataOut)
    {
        acDataOut = _acDataOut;
    }
    /**
    * The purpose of this command is to send (copy) a loaded object from the TPM to an Attached Component.
    */
    public AC_SendResponse() {};
    /**
    * May include AC specific data or information about an error.
    */
    public TPMS_AC_OUTPUT acDataOut;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        acDataOut.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        acDataOut = TPMS_AC_OUTPUT.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static AC_SendResponse fromTpm (byte[] x) 
    {
        AC_SendResponse ret = new AC_SendResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static AC_SendResponse fromTpm (InByteBuf buf) 
    {
        AC_SendResponse ret = new AC_SendResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_AC_Send_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_AC_OUTPUT", "acDataOut", acDataOut);
    };
    
    
};

//<<<

