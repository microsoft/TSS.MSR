package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command will take the actual field upgrade image to be installed on the TPM. The exact format of fuData is vendor-specific. This command is only possible following a successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
*/
public class TPM2_FieldUpgradeData_REQUEST extends TpmStructure
{
    /**
     * This command will take the actual field upgrade image to be installed on the TPM. The exact format of fuData is vendor-specific. This command is only possible following a successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
     * 
     * @param _fuData field upgrade image data
     */
    public TPM2_FieldUpgradeData_REQUEST(byte[] _fuData)
    {
        fuData = _fuData;
    }
    /**
    * This command will take the actual field upgrade image to be installed on the TPM. The exact format of fuData is vendor-specific. This command is only possible following a successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
    */
    public TPM2_FieldUpgradeData_REQUEST() {};
    /**
    * size of the buffer
    */
    // private short fuDataSize;
    /**
    * field upgrade image data
    */
    public byte[] fuData;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((fuData!=null)?fuData.length:0, 2);
        if(fuData!=null)
            buf.write(fuData);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _fuDataSize = buf.readInt(2);
        fuData = new byte[_fuDataSize];
        buf.readArrayOfInts(fuData, 1, _fuDataSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_FieldUpgradeData_REQUEST fromTpm (byte[] x) 
    {
        TPM2_FieldUpgradeData_REQUEST ret = new TPM2_FieldUpgradeData_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_FieldUpgradeData_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_FieldUpgradeData_REQUEST ret = new TPM2_FieldUpgradeData_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FieldUpgradeData_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "fuData", fuData);
    };
    
    
};

//<<<

