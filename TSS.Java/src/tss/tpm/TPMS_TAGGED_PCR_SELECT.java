package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used in TPM2_GetCapability() to return the attributes of the PCR.  */
public class TPMS_TAGGED_PCR_SELECT extends TpmStructure
{
    /** The property identifier  */
    public TPM_PT_PCR tag;
    
    /** The bit map of PCR with the identified property  */
    public byte[] pcrSelect;
    
    public TPMS_TAGGED_PCR_SELECT() {}
    
    /** @param _tag The property identifier
     *  @param _pcrSelect The bit map of PCR with the identified property
     */
    public TPMS_TAGGED_PCR_SELECT(TPM_PT_PCR _tag, byte[] _pcrSelect)
    {
        tag = _tag;
        pcrSelect = _pcrSelect;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        tag.toTpm(buf);
        buf.writeSizedByteBuf(pcrSelect, 1);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        tag = TPM_PT_PCR.fromTpm(buf);
        int _sizeofSelect = buf.readByte();
        pcrSelect = new byte[_sizeofSelect];
        buf.readArrayOfInts(pcrSelect, 1, _sizeofSelect);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMS_TAGGED_PCR_SELECT fromBytes (byte[] byteBuf) 
    {
        TPMS_TAGGED_PCR_SELECT ret = new TPMS_TAGGED_PCR_SELECT();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_TAGGED_PCR_SELECT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPMS_TAGGED_PCR_SELECT fromTpm (InByteBuf buf) 
    {
        TPMS_TAGGED_PCR_SELECT ret = new TPMS_TAGGED_PCR_SELECT();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_TAGGED_PCR_SELECT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_PT_PCR", "tag", tag);
        _p.add(d, "byte", "pcrSelect", pcrSelect);
    }
}

//<<<
