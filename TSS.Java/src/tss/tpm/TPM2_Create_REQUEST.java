package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command is used to create an object that can be loaded into a TPM using TPM2_Load().
 *  If the command completes successfully, the TPM will create the new object and return the
 *  objects creation data (creationData), its public area (outPublic), and its encrypted
 *  sensitive area (outPrivate). Preservation of the returned data is the responsibility of
 *  the caller. The object will need to be loaded (TPM2_Load()) before it may be used. The
 *  only difference between the inPublic TPMT_PUBLIC template and the outPublic TPMT_PUBLIC
 *  object is in the unique field.
 */
public class TPM2_Create_REQUEST extends TpmStructure
{
    /**
     *  handle of parent for new object
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE parentHandle;
    
    /** the sensitive data */
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
    
    public TPM2_Create_REQUEST() { parentHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _parentHandle handle of parent for new object
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _inSensitive the sensitive data
     *  @param _inPublic the public template
     *  @param _outsideInfo data that will be included in the creation data for this object to provide permanent,
     *         verifiable linkage between this object and some object owner data
     *  @param _creationPCR PCR that will be used in creation data
     */
    public TPM2_Create_REQUEST(TPM_HANDLE _parentHandle, TPMS_SENSITIVE_CREATE _inSensitive, TPMT_PUBLIC _inPublic, byte[] _outsideInfo, TPMS_PCR_SELECTION[] _creationPCR)
    {
        parentHandle = _parentHandle;
        inSensitive = _inSensitive;
        inPublic = _inPublic;
        outsideInfo = _outsideInfo;
        creationPCR = _creationPCR;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
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
        return buf.buffer();
    }

    public static TPM2_Create_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Create_REQUEST ret = new TPM2_Create_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_Create_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Create_REQUEST ret = new TPM2_Create_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Create_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "parentHandle", parentHandle);
        _p.add(d, "TPMS_SENSITIVE_CREATE", "inSensitive", inSensitive);
        _p.add(d, "TPMT_PUBLIC", "inPublic", inPublic);
        _p.add(d, "byte", "outsideInfo", outsideInfo);
        _p.add(d, "TPMS_PCR_SELECTION", "creationPCR", creationPCR);
    }
}

//<<<
