package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command will take the actual field upgrade image to be installed on the TPM. The exact format of fuData is vendor-specific. This command is only possible following a successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
*/
public class FieldUpgradeDataResponse extends TpmStructure
{
    /**
     * This command will take the actual field upgrade image to be installed on the TPM. The exact format of fuData is vendor-specific. This command is only possible following a successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
     * 
     * @param _nextDigest tagged digest of the next block TPM_ALG_NULL if field update is complete 
     * @param _firstDigest tagged digest of the first block of the sequence
     */
    public FieldUpgradeDataResponse(TPMT_HA _nextDigest,TPMT_HA _firstDigest)
    {
        nextDigest = _nextDigest;
        firstDigest = _firstDigest;
    }
    /**
    * This command will take the actual field upgrade image to be installed on the TPM. The exact format of fuData is vendor-specific. This command is only possible following a successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
    */
    public FieldUpgradeDataResponse() {};
    /**
    * tagged digest of the next block TPM_ALG_NULL if field update is complete
    */
    public TPMT_HA nextDigest;
    /**
    * tagged digest of the first block of the sequence
    */
    public TPMT_HA firstDigest;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        nextDigest.toTpm(buf);
        firstDigest.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        // TODO TpmHash  -- 
        // TODO TpmHash  -- 
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static FieldUpgradeDataResponse fromTpm (byte[] x) 
    {
        FieldUpgradeDataResponse ret = new FieldUpgradeDataResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static FieldUpgradeDataResponse fromTpm (InByteBuf buf) 
    {
        FieldUpgradeDataResponse ret = new FieldUpgradeDataResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FieldUpgradeData_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TpmHash", "nextDigest", nextDigest);
        _p.add(d, "TpmHash", "firstDigest", firstDigest);
    };
    
    
};

//<<<

