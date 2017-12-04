package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This is the attested data for TPM2_GetCommandAuditDigest().
*/
public class TPMS_COMMAND_AUDIT_INFO extends TpmStructure implements TPMU_ATTEST 
{
    /**
     * This is the attested data for TPM2_GetCommandAuditDigest().
     * 
     * @param _auditCounter the monotonic audit counter 
     * @param _digestAlg hash algorithm used for the command audit 
     * @param _auditDigest the current value of the audit digest 
     * @param _commandDigest digest of the command codes being audited using digestAlg
     */
    public TPMS_COMMAND_AUDIT_INFO(long _auditCounter,TPM_ALG_ID _digestAlg,byte[] _auditDigest,byte[] _commandDigest)
    {
        auditCounter = _auditCounter;
        digestAlg = _digestAlg;
        auditDigest = _auditDigest;
        commandDigest = _commandDigest;
    }
    /**
    * This is the attested data for TPM2_GetCommandAuditDigest().
    */
    public TPMS_COMMAND_AUDIT_INFO() {};
    /**
    * the monotonic audit counter
    */
    public long auditCounter;
    /**
    * hash algorithm used for the command audit
    */
    public TPM_ALG_ID digestAlg;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short auditDigestSize;
    /**
    * the current value of the audit digest
    */
    public byte[] auditDigest;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short commandDigestSize;
    /**
    * digest of the command codes being audited using digestAlg
    */
    public byte[] commandDigest;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.write(auditCounter);
        digestAlg.toTpm(buf);
        buf.writeInt((auditDigest!=null)?auditDigest.length:0, 2);
        if(auditDigest!=null)
            buf.write(auditDigest);
        buf.writeInt((commandDigest!=null)?commandDigest.length:0, 2);
        if(commandDigest!=null)
            buf.write(commandDigest);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        auditCounter = buf.readLong();
        digestAlg = TPM_ALG_ID.fromTpm(buf);
        int _auditDigestSize = buf.readInt(2);
        auditDigest = new byte[_auditDigestSize];
        buf.readArrayOfInts(auditDigest, 1, _auditDigestSize);
        int _commandDigestSize = buf.readInt(2);
        commandDigest = new byte[_commandDigestSize];
        buf.readArrayOfInts(commandDigest, 1, _commandDigestSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_COMMAND_AUDIT_INFO fromTpm (byte[] x) 
    {
        TPMS_COMMAND_AUDIT_INFO ret = new TPMS_COMMAND_AUDIT_INFO();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_COMMAND_AUDIT_INFO fromTpm (InByteBuf buf) 
    {
        TPMS_COMMAND_AUDIT_INFO ret = new TPMS_COMMAND_AUDIT_INFO();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_COMMAND_AUDIT_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "ulong", "auditCounter", auditCounter);
        _p.add(d, "TPM_ALG_ID", "digestAlg", digestAlg);
        _p.add(d, "byte", "auditDigest", auditDigest);
        _p.add(d, "byte", "commandDigest", commandDigest);
    };
    
    
};

//<<<

