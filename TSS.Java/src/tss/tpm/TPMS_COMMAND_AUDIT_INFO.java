package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the attested data for TPM2_GetCommandAuditDigest().  */
public class TPMS_COMMAND_AUDIT_INFO extends TpmStructure implements TPMU_ATTEST
{
    /** The monotonic audit counter  */
    public long auditCounter;
    
    /** Hash algorithm used for the command audit  */
    public TPM_ALG_ID digestAlg;
    
    /** The current value of the audit digest  */
    public byte[] auditDigest;
    
    /** Digest of the command codes being audited using digestAlg  */
    public byte[] commandDigest;
    
    public TPMS_COMMAND_AUDIT_INFO() { digestAlg = TPM_ALG_ID.NULL; }
    
    /** @param _auditCounter The monotonic audit counter
     *  @param _digestAlg Hash algorithm used for the command audit
     *  @param _auditDigest The current value of the audit digest
     *  @param _commandDigest Digest of the command codes being audited using digestAlg
     */
    public TPMS_COMMAND_AUDIT_INFO(long _auditCounter, TPM_ALG_ID _digestAlg, byte[] _auditDigest, byte[] _commandDigest)
    {
        auditCounter = _auditCounter;
        digestAlg = _digestAlg;
        auditDigest = _auditDigest;
        commandDigest = _commandDigest;
    }
    
    /** TpmUnion method  */
    public TPM_ST GetUnionSelector() { return TPM_ST.ATTEST_COMMAND_AUDIT; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt64(auditCounter);
        digestAlg.toTpm(buf);
        buf.writeSizedByteBuf(auditDigest);
        buf.writeSizedByteBuf(commandDigest);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        auditCounter = buf.readInt64();
        digestAlg = TPM_ALG_ID.fromTpm(buf);
        int _auditDigestSize = buf.readShort() & 0xFFFF;
        auditDigest = new byte[_auditDigestSize];
        buf.readArrayOfInts(auditDigest, 1, _auditDigestSize);
        int _commandDigestSize = buf.readShort() & 0xFFFF;
        commandDigest = new byte[_commandDigestSize];
        buf.readArrayOfInts(commandDigest, 1, _commandDigestSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMS_COMMAND_AUDIT_INFO fromBytes (byte[] byteBuf) 
    {
        TPMS_COMMAND_AUDIT_INFO ret = new TPMS_COMMAND_AUDIT_INFO();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_COMMAND_AUDIT_INFO fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
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
        _p.add(d, "long", "auditCounter", auditCounter);
        _p.add(d, "TPM_ALG_ID", "digestAlg", digestAlg);
        _p.add(d, "byte", "auditDigest", auditDigest);
        _p.add(d, "byte", "commandDigest", commandDigest);
    }
}

//<<<
