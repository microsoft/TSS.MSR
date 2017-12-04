package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command may be used by the Privacy Administrator or platform to change the audit status of a command or to set the hash algorithm used for the audit digest, but not both at the same time.
*/
public class TPM2_SetCommandCodeAuditStatus_REQUEST extends TpmStructure
{
    /**
     * This command may be used by the Privacy Administrator or platform to change the audit status of a command or to set the hash algorithm used for the audit digest, but not both at the same time.
     * 
     * @param _auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param _auditAlg hash algorithm for the audit digest; if TPM_ALG_NULL, then the hash is not changed 
     * @param _setList list of commands that will be added to those that will be audited 
     * @param _clearList list of commands that will no longer be audited
     */
    public TPM2_SetCommandCodeAuditStatus_REQUEST(TPM_HANDLE _auth,TPM_ALG_ID _auditAlg,TPM_CC[] _setList,TPM_CC[] _clearList)
    {
        auth = _auth;
        auditAlg = _auditAlg;
        setList = _setList;
        clearList = _clearList;
    }
    /**
    * This command may be used by the Privacy Administrator or platform to change the audit status of a command or to set the hash algorithm used for the audit digest, but not both at the same time.
    */
    public TPM2_SetCommandCodeAuditStatus_REQUEST() {};
    /**
    * TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE auth;
    /**
    * hash algorithm for the audit digest; if TPM_ALG_NULL, then the hash is not changed
    */
    public TPM_ALG_ID auditAlg;
    /**
    * number of commands in the commandCode list; may be 0
    */
    // private int setListCount;
    /**
    * list of commands that will be added to those that will be audited
    */
    public TPM_CC[] setList;
    /**
    * number of commands in the commandCode list; may be 0
    */
    // private int clearListCount;
    /**
    * list of commands that will no longer be audited
    */
    public TPM_CC[] clearList;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        auth.toTpm(buf);
        auditAlg.toTpm(buf);
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
        auditAlg = TPM_ALG_ID.fromTpm(buf);
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
    public static TPM2_SetCommandCodeAuditStatus_REQUEST fromTpm (byte[] x) 
    {
        TPM2_SetCommandCodeAuditStatus_REQUEST ret = new TPM2_SetCommandCodeAuditStatus_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_SetCommandCodeAuditStatus_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_SetCommandCodeAuditStatus_REQUEST ret = new TPM2_SetCommandCodeAuditStatus_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SetCommandCodeAuditStatus_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "TPM_ALG_ID", "auditAlg", auditAlg);
        _p.add(d, "TPM_CC", "setList", setList);
        _p.add(d, "TPM_CC", "clearList", clearList);
    };
    
    
};

//<<<

