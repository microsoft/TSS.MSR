package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command is used to create a Primary Object under one of the Primary Seeds or a
 *  Temporary Object under TPM_RH_NULL. The command uses a TPM2B_PUBLIC as a template for the
 *  object to be created. The size of the unique field shall not be checked for consistency
 *  with the other object parameters. The command will create and load a Primary Object. The
 *  sensitive area is not returned.
 */
public class TPM2_CreatePrimary_REQUEST extends TpmStructure
{
    /**
     *  TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE primaryHandle;
    
    /** the sensitive data, see TPM 2.0 Part 1 Sensitive Values */
    public TPMS_SENSITIVE_CREATE inSensitive;
    
    /** the public template */
    public TPMT_PUBLIC inPublic;
    
    /**
     *  data that will be included in the creation data for this object to provide permanent,
     *  verifiable linkage between this object and some object owner data
     */
    public byte[] outsideInfo;
    
    /** PCR that will be used in creation data */
    public TPMS_PCR_SELECTION[] creationPCR;
    
    public TPM2_CreatePrimary_REQUEST() { primaryHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _primaryHandle TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _inSensitive the sensitive data, see TPM 2.0 Part 1 Sensitive Values
     *  @param _inPublic the public template
     *  @param _outsideInfo data that will be included in the creation data for this object to provide permanent,
     *         verifiable linkage between this object and some object owner data
     *  @param _creationPCR PCR that will be used in creation data
     */
    public TPM2_CreatePrimary_REQUEST(TPM_HANDLE _primaryHandle, TPMS_SENSITIVE_CREATE _inSensitive, TPMT_PUBLIC _inPublic, byte[] _outsideInfo, TPMS_PCR_SELECTION[] _creationPCR)
    {
        primaryHandle = _primaryHandle;
        inSensitive = _inSensitive;
        inPublic = _inPublic;
        outsideInfo = _outsideInfo;
        creationPCR = _creationPCR;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        primaryHandle.toTpm(buf);
        buf.writeShort(inSensitive != null ? inSensitive.toTpm().length : 0);
        if (inSensitive != null)
            inSensitive.toTpm(buf);
        buf.writeShort(inPublic != null ? inPublic.toTpm().length : 0);
        if (inPublic != null)
            inPublic.toTpm(buf);
        buf.writeSizedByteBuf(outsideInfo);
        buf.writeObjArr(creationPCR);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        primaryHandle = TPM_HANDLE.fromTpm(buf);
        int _inSensitiveSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inSensitiveSize));
        inSensitive = TPMS_SENSITIVE_CREATE.fromTpm(buf);
        buf.structSize.pop();
        int _inPublicSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inPublicSize));
        inPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
        int _outsideInfoSize = buf.readShort() & 0xFFFF;
        outsideInfo = new byte[_outsideInfoSize];
        buf.readArrayOfInts(outsideInfo, 1, _outsideInfoSize);
        int _creationPCRCount = buf.readInt();
        creationPCR = new TPMS_PCR_SELECTION[_creationPCRCount];
        for (int j=0; j < _creationPCRCount; j++) creationPCR[j] = new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(creationPCR, _creationPCRCount);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_CreatePrimary_REQUEST fromTpm (byte[] x) 
    {
        TPM2_CreatePrimary_REQUEST ret = new TPM2_CreatePrimary_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_CreatePrimary_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_CreatePrimary_REQUEST ret = new TPM2_CreatePrimary_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CreatePrimary_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "primaryHandle", primaryHandle);
        _p.add(d, "TPMS_SENSITIVE_CREATE", "inSensitive", inSensitive);
        _p.add(d, "TPMT_PUBLIC", "inPublic", inPublic);
        _p.add(d, "byte", "outsideInfo", outsideInfo);
        _p.add(d, "TPMS_PCR_SELECTION", "creationPCR", creationPCR);
    }
}

//<<<
