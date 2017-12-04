package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* The purpose of this command is to obtain information about an Attached Component referenced by an AC handle.
*/
public class TPM2_AC_GetCapability_REQUEST extends TpmStructure
{
    /**
     * The purpose of this command is to obtain information about an Attached Component referenced by an AC handle.
     * 
     * @param _ac handle indicating the Attached Component Auth Index: None 
     * @param _capability starting info type 
     * @param _count maximum number of values to return
     */
    public TPM2_AC_GetCapability_REQUEST(TPM_HANDLE _ac,TPM_AT _capability,int _count)
    {
        ac = _ac;
        capability = _capability;
        count = _count;
    }
    /**
    * The purpose of this command is to obtain information about an Attached Component referenced by an AC handle.
    */
    public TPM2_AC_GetCapability_REQUEST() {};
    /**
    * handle indicating the Attached Component Auth Index: None
    */
    public TPM_HANDLE ac;
    /**
    * starting info type
    */
    public TPM_AT capability;
    /**
    * maximum number of values to return
    */
    public int count;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        ac.toTpm(buf);
        capability.toTpm(buf);
        buf.write(count);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        ac = TPM_HANDLE.fromTpm(buf);
        capability = TPM_AT.fromTpm(buf);
        count =  buf.readInt(4);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_AC_GetCapability_REQUEST fromTpm (byte[] x) 
    {
        TPM2_AC_GetCapability_REQUEST ret = new TPM2_AC_GetCapability_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_AC_GetCapability_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_AC_GetCapability_REQUEST ret = new TPM2_AC_GetCapability_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_AC_GetCapability_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "ac", ac);
        _p.add(d, "TPM_AT", "capability", capability);
        _p.add(d, "uint", "count", count);
    };
    
    
};

//<<<

