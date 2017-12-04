package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to create an object that can be loaded into a TPM using TPM2_Load(). If the command completes successfully, the TPM will create the new object and return the objects creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate). Preservation of the returned data is the responsibility of the caller. The object will need to be loaded (TPM2_Load()) before it may be used. The only difference between the inPublic TPMT_PUBLIC template and the outPublic TPMT_PUBLIC object is in the unique field.
*/
public class TPM2_Create_REQUEST extends TpmStructure
{
    /**
     * This command is used to create an object that can be loaded into a TPM using TPM2_Load(). If the command completes successfully, the TPM will create the new object and return the objects creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate). Preservation of the returned data is the responsibility of the caller. The object will need to be loaded (TPM2_Load()) before it may be used. The only difference between the inPublic TPMT_PUBLIC template and the outPublic TPMT_PUBLIC object is in the unique field.
     * 
     * @param _parentHandle handle of parent for new object Auth Index: 1 Auth Role: USER 
     * @param _inSensitive the sensitive data 
     * @param _inPublic the public template 
     * @param _outsideInfo data that will be included in the creation data for this object to provide permanent, verifiable linkage between this object and some object owner data 
     * @param _creationPCR PCR that will be used in creation data
     */
    public TPM2_Create_REQUEST(TPM_HANDLE _parentHandle,TPMS_SENSITIVE_CREATE _inSensitive,TPMT_PUBLIC _inPublic,byte[] _outsideInfo,TPMS_PCR_SELECTION[] _creationPCR)
    {
        parentHandle = _parentHandle;
        inSensitive = _inSensitive;
        inPublic = _inPublic;
        outsideInfo = _outsideInfo;
        creationPCR = _creationPCR;
    }
    /**
    * This command is used to create an object that can be loaded into a TPM using TPM2_Load(). If the command completes successfully, the TPM will create the new object and return the objects creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate). Preservation of the returned data is the responsibility of the caller. The object will need to be loaded (TPM2_Load()) before it may be used. The only difference between the inPublic TPMT_PUBLIC template and the outPublic TPMT_PUBLIC object is in the unique field.
    */
    public TPM2_Create_REQUEST() {};
    /**
    * handle of parent for new object Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE parentHandle;
    /**
    * size of sensitive in octets (may not be zero) NOTE The userAuth and data parameters in this buffer may both be zero length but the minimum size of this parameter will be the sum of the size fields of the two parameters of the TPMS_SENSITIVE_CREATE.
    */
    // private short inSensitiveSize;
    /**
    * the sensitive data
    */
    public TPMS_SENSITIVE_CREATE inSensitive;
    /**
    * size of publicArea NOTE The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.
    */
    // private short inPublicSize;
    /**
    * the public template
    */
    public TPMT_PUBLIC inPublic;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short outsideInfoSize;
    /**
    * data that will be included in the creation data for this object to provide permanent, verifiable linkage between this object and some object owner data
    */
    public byte[] outsideInfo;
    /**
    * number of selection structures A value of zero is allowed.
    */
    // private int creationPCRCount;
    /**
    * PCR that will be used in creation data
    */
    public TPMS_PCR_SELECTION[] creationPCR;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        parentHandle.toTpm(buf);
        buf.writeInt((inSensitive!=null)?inSensitive.toTpm().length:0, 2);
        if(inSensitive!=null)
            inSensitive.toTpm(buf);
        buf.writeInt((inPublic!=null)?inPublic.toTpm().length:0, 2);
        if(inPublic!=null)
            inPublic.toTpm(buf);
        buf.writeInt((outsideInfo!=null)?outsideInfo.length:0, 2);
        if(outsideInfo!=null)
            buf.write(outsideInfo);
        buf.writeInt((creationPCR!=null)?creationPCR.length:0, 4);
        if(creationPCR!=null)
            buf.writeArrayOfTpmObjects(creationPCR);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        parentHandle = TPM_HANDLE.fromTpm(buf);
        int _inSensitiveSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inSensitiveSize));
        inSensitive = TPMS_SENSITIVE_CREATE.fromTpm(buf);
        buf.structSize.pop();
        int _inPublicSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inPublicSize));
        inPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
        int _outsideInfoSize = buf.readInt(2);
        outsideInfo = new byte[_outsideInfoSize];
        buf.readArrayOfInts(outsideInfo, 1, _outsideInfoSize);
        int _creationPCRCount = buf.readInt(4);
        creationPCR = new TPMS_PCR_SELECTION[_creationPCRCount];
        for(int j=0;j<_creationPCRCount;j++)creationPCR[j]=new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(creationPCR, _creationPCRCount);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
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
    };
    
    
};

//<<<

