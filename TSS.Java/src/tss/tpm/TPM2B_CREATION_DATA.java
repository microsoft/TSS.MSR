package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is created by TPM2_Create() and TPM2_CreatePrimary(). It is never entered into the TPM and never has a size of zero.
*/
public class TPM2B_CREATION_DATA extends TpmStructure
{
    /**
     * This structure is created by TPM2_Create() and TPM2_CreatePrimary(). It is never entered into the TPM and never has a size of zero.
     * 
     * @param _creationData -
     */
    public TPM2B_CREATION_DATA(TPMS_CREATION_DATA _creationData)
    {
        creationData = _creationData;
    }
    /**
    * This structure is created by TPM2_Create() and TPM2_CreatePrimary(). It is never entered into the TPM and never has a size of zero.
    */
    public TPM2B_CREATION_DATA() {};
    /**
    * size of the creation data
    */
    // private short size;
    public TPMS_CREATION_DATA creationData;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((creationData!=null)?creationData.toTpm().length:0, 2);
        if(creationData!=null)
            creationData.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _size));
        creationData = TPMS_CREATION_DATA.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2B_CREATION_DATA fromTpm (byte[] x) 
    {
        TPM2B_CREATION_DATA ret = new TPM2B_CREATION_DATA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_CREATION_DATA fromTpm (InByteBuf buf) 
    {
        TPM2B_CREATION_DATA ret = new TPM2B_CREATION_DATA();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_CREATION_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_CREATION_DATA", "creationData", creationData);
    };
    
    
};

//<<<

