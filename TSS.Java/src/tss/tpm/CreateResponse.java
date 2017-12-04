package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to create an object that can be loaded into a TPM using TPM2_Load(). If the command completes successfully, the TPM will create the new object and return the objects creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate). Preservation of the returned data is the responsibility of the caller. The object will need to be loaded (TPM2_Load()) before it may be used. The only difference between the inPublic TPMT_PUBLIC template and the outPublic TPMT_PUBLIC object is in the unique field.
*/
public class CreateResponse extends TpmStructure
{
    /**
     * This command is used to create an object that can be loaded into a TPM using TPM2_Load(). If the command completes successfully, the TPM will create the new object and return the objects creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate). Preservation of the returned data is the responsibility of the caller. The object will need to be loaded (TPM2_Load()) before it may be used. The only difference between the inPublic TPMT_PUBLIC template and the outPublic TPMT_PUBLIC object is in the unique field.
     * 
     * @param _outPrivate the private portion of the object 
     * @param _outPublic the public portion of the created object 
     * @param _creationData contains a TPMS_CREATION_DATA 
     * @param _creationHash digest of creationData using nameAlg of outPublic 
     * @param _creationTicket ticket used by TPM2_CertifyCreation() to validate that the creation data was produced by the TPM
     */
    public CreateResponse(TPM2B_PRIVATE _outPrivate,TPMT_PUBLIC _outPublic,TPMS_CREATION_DATA _creationData,byte[] _creationHash,TPMT_TK_CREATION _creationTicket)
    {
        outPrivate = _outPrivate;
        outPublic = _outPublic;
        creationData = _creationData;
        creationHash = _creationHash;
        creationTicket = _creationTicket;
    }
    /**
    * This command is used to create an object that can be loaded into a TPM using TPM2_Load(). If the command completes successfully, the TPM will create the new object and return the objects creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate). Preservation of the returned data is the responsibility of the caller. The object will need to be loaded (TPM2_Load()) before it may be used. The only difference between the inPublic TPMT_PUBLIC template and the outPublic TPMT_PUBLIC object is in the unique field.
    */
    public CreateResponse() {};
    /**
    * the private portion of the object
    */
    public TPM2B_PRIVATE outPrivate;
    /**
    * size of publicArea NOTE The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.
    */
    // private short outPublicSize;
    /**
    * the public portion of the created object
    */
    public TPMT_PUBLIC outPublic;
    /**
    * size of the creation data
    */
    // private short creationDataSize;
    /**
    * contains a TPMS_CREATION_DATA
    */
    public TPMS_CREATION_DATA creationData;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short creationHashSize;
    /**
    * digest of creationData using nameAlg of outPublic
    */
    public byte[] creationHash;
    /**
    * ticket used by TPM2_CertifyCreation() to validate that the creation data was produced by the TPM
    */
    public TPMT_TK_CREATION creationTicket;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        outPrivate.toTpm(buf);
        buf.writeInt((outPublic!=null)?outPublic.toTpm().length:0, 2);
        if(outPublic!=null)
            outPublic.toTpm(buf);
        buf.writeInt((creationData!=null)?creationData.toTpm().length:0, 2);
        if(creationData!=null)
            creationData.toTpm(buf);
        buf.writeInt((creationHash!=null)?creationHash.length:0, 2);
        if(creationHash!=null)
            buf.write(creationHash);
        creationTicket.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        outPrivate = TPM2B_PRIVATE.fromTpm(buf);
        int _outPublicSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _outPublicSize));
        outPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
        int _creationDataSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _creationDataSize));
        creationData = TPMS_CREATION_DATA.fromTpm(buf);
        buf.structSize.pop();
        int _creationHashSize = buf.readInt(2);
        creationHash = new byte[_creationHashSize];
        buf.readArrayOfInts(creationHash, 1, _creationHashSize);
        creationTicket = TPMT_TK_CREATION.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static CreateResponse fromTpm (byte[] x) 
    {
        CreateResponse ret = new CreateResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static CreateResponse fromTpm (InByteBuf buf) 
    {
        CreateResponse ret = new CreateResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Create_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM2B_PRIVATE", "outPrivate", outPrivate);
        _p.add(d, "TPMT_PUBLIC", "outPublic", outPublic);
        _p.add(d, "TPMS_CREATION_DATA", "creationData", creationData);
        _p.add(d, "byte", "creationHash", creationHash);
        _p.add(d, "TPMT_TK_CREATION", "creationTicket", creationTicket);
    };
    
    
};

//<<<

