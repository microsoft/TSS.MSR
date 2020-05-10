package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the attested data for TPM2_CertifyCreation(). */
public class TPMS_CREATION_INFO extends TpmStructure implements TPMU_ATTEST
{
    /** Name of the object */
    public byte[] objectName;
    
    /** creationHash */
    public byte[] creationHash;
    
    public TPMS_CREATION_INFO() {}
    
    /**
     *  @param _objectName Name of the object
     *  @param _creationHash creationHash
     */
    public TPMS_CREATION_INFO(byte[] _objectName, byte[] _creationHash)
    {
        objectName = _objectName;
        creationHash = _creationHash;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(objectName);
        buf.writeSizedByteBuf(creationHash);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _objectNameSize = buf.readShort() & 0xFFFF;
        objectName = new byte[_objectNameSize];
        buf.readArrayOfInts(objectName, 1, _objectNameSize);
        int _creationHashSize = buf.readShort() & 0xFFFF;
        creationHash = new byte[_creationHashSize];
        buf.readArrayOfInts(creationHash, 1, _creationHashSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPMS_CREATION_INFO fromTpm (byte[] x) 
    {
        TPMS_CREATION_INFO ret = new TPMS_CREATION_INFO();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_CREATION_INFO fromTpm (InByteBuf buf) 
    {
        TPMS_CREATION_INFO ret = new TPMS_CREATION_INFO();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CREATION_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "objectName", objectName);
        _p.add(d, "byte", "creationHash", creationHash);
    }
}

//<<<

