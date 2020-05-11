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
public class CreatePrimaryResponse extends TpmStructure
{
    /** handle of type TPM_HT_TRANSIENT for created Primary Object */
    public TPM_HANDLE handle;
    
    /** the public portion of the created object */
    public TPMT_PUBLIC outPublic;
    
    /** contains a TPMT_CREATION_DATA */
    public TPMS_CREATION_DATA creationData;
    
    /** digest of creationData using nameAlg of outPublic */
    public byte[] creationHash;
    
    /**
     *  ticket used by TPM2_CertifyCreation() to validate that the creation data
     *  was produced by the TPM
     */
    public TPMT_TK_CREATION creationTicket;
    
    /** the name of the created object */
    public byte[] name;
    
    public CreatePrimaryResponse() { handle = new TPM_HANDLE(); }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        buf.writeShort(outPublic != null ? outPublic.toTpm().length : 0);
        if (outPublic != null)
            outPublic.toTpm(buf);
        buf.writeShort(creationData != null ? creationData.toTpm().length : 0);
        if (creationData != null)
            creationData.toTpm(buf);
        buf.writeSizedByteBuf(creationHash);
        creationTicket.toTpm(buf);
        buf.writeSizedByteBuf(name);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        int _outPublicSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _outPublicSize));
        outPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
        int _creationDataSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _creationDataSize));
        creationData = TPMS_CREATION_DATA.fromTpm(buf);
        buf.structSize.pop();
        int _creationHashSize = buf.readShort() & 0xFFFF;
        creationHash = new byte[_creationHashSize];
        buf.readArrayOfInts(creationHash, 1, _creationHashSize);
        creationTicket = TPMT_TK_CREATION.fromTpm(buf);
        int _nameSize = buf.readShort() & 0xFFFF;
        name = new byte[_nameSize];
        buf.readArrayOfInts(name, 1, _nameSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static CreatePrimaryResponse fromTpm (byte[] x) 
    {
        CreatePrimaryResponse ret = new CreatePrimaryResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static CreatePrimaryResponse fromTpm (InByteBuf buf) 
    {
        CreatePrimaryResponse ret = new CreatePrimaryResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CreatePrimary_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "TPMT_PUBLIC", "outPublic", outPublic);
        _p.add(d, "TPMS_CREATION_DATA", "creationData", creationData);
        _p.add(d, "byte", "creationHash", creationHash);
        _p.add(d, "TPMT_TK_CREATION", "creationTicket", creationTicket);
        _p.add(d, "byte", "name", name);
    }
}

//<<<
