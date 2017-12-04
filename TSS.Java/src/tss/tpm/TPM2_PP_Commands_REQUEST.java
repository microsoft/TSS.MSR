package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to determine which commands require assertion of Physical Presence (PP) in addition to platformAuth/platformPolicy.
*/
public class TPM2_PP_Commands_REQUEST extends TpmStructure
{
    /**
     * This command is used to determine which commands require assertion of Physical Presence (PP) in addition to platformAuth/platformPolicy.
     * 
     * @param _auth TPM_RH_PLATFORM+PP Auth Index: 1 Auth Role: USER + Physical Presence 
     * @param _setList list of commands to be added to those that will require that Physical Presence be asserted 
     * @param _clearList list of commands that will no longer require that Physical Presence be asserted
     */
    public TPM2_PP_Commands_REQUEST(TPM_HANDLE _auth,TPM_CC[] _setList,TPM_CC[] _clearList)
    {
        auth = _auth;
        setList = _setList;
        clearList = _clearList;
    }
    /**
    * This command is used to determine which commands require assertion of Physical Presence (PP) in addition to platformAuth/platformPolicy.
    */
    public TPM2_PP_Commands_REQUEST() {};
    /**
    * TPM_RH_PLATFORM+PP Auth Index: 1 Auth Role: USER + Physical Presence
    */
    public TPM_HANDLE auth;
    /**
    * number of commands in the commandCode list; may be 0
    */
    // private int setListCount;
    /**
    * list of commands to be added to those that will require that Physical Presence be asserted
    */
    public TPM_CC[] setList;
    /**
    * number of commands in the commandCode list; may be 0
    */
    // private int clearListCount;
    /**
    * list of commands that will no longer require that Physical Presence be asserted
    */
    public TPM_CC[] clearList;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        auth.toTpm(buf);
        buf.writeInt((setList!=null)?setList.length:0, 4);
        if(setList!=null)
            buf.writeArrayOfTpmObjects(setList);
        buf.writeInt((clearList!=null)?clearList.length:0, 4);
        if(clearList!=null)
            buf.writeArrayOfTpmObjects(clearList);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        auth = TPM_HANDLE.fromTpm(buf);
        int _setListCount = buf.readInt(4);
        setList = new TPM_CC[_setListCount];
        for(int j=0;j<_setListCount;j++){setList[j]=TPM_CC.fromTpm(buf);};
        int _clearListCount = buf.readInt(4);
        clearList = new TPM_CC[_clearListCount];
        for(int j=0;j<_clearListCount;j++){clearList[j]=TPM_CC.fromTpm(buf);};
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PP_Commands_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PP_Commands_REQUEST ret = new TPM2_PP_Commands_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PP_Commands_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PP_Commands_REQUEST ret = new TPM2_PP_Commands_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PP_Commands_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "TPM_CC", "setList", setList);
        _p.add(d, "TPM_CC", "clearList", clearList);
    };
    
    
};

//<<<

