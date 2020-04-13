package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This list is used to report the timeout and state for the ACT. This list may be generated
 *  by TPM2_GetCapabilty(). Only implemented ACT are present in the list
 */
public class TPML_ACT_DATA extends TpmStructure implements TPMU_CAPABILITIES 
{
    /** array of ACT data */
    public TPMS_ACT_DATA[] actData;
    
    public TPML_ACT_DATA() {}
    
    /** @param _actData array of ACT data */
    public TPML_ACT_DATA(TPMS_ACT_DATA[] _actData)
    {
        actData = _actData;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(actData != null ? actData.length : 0, 4);
        if (actData != null)
            buf.writeArrayOfTpmObjects(actData);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _count = buf.readInt(4);
        actData = new TPMS_ACT_DATA[_count];
        for (int j=0; j < _count; j++) actData[j] = new TPMS_ACT_DATA();
        buf.readArrayOfTpmObjects(actData, _count);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPML_ACT_DATA fromTpm (byte[] x) 
    {
        TPML_ACT_DATA ret = new TPML_ACT_DATA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPML_ACT_DATA fromTpm (InByteBuf buf) 
    {
        TPML_ACT_DATA ret = new TPML_ACT_DATA();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_ACT_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ACT_DATA", "actData", actData);
    }
}

//<<<

