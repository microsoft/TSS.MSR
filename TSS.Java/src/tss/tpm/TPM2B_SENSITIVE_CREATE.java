package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure contains the sensitive creation data in a sized buffer. This structure is defined so that both the userAuth and data values of the TPMS_SENSITIVE_CREATE may be passed as a single parameter for parameter encryption purposes.
*/
public class TPM2B_SENSITIVE_CREATE extends TpmStructure
{
    /**
     * This structure contains the sensitive creation data in a sized buffer. This structure is defined so that both the userAuth and data values of the TPMS_SENSITIVE_CREATE may be passed as a single parameter for parameter encryption purposes.
     * 
     * @param _sensitive data to be sealed or a symmetric key value.
     */
    public TPM2B_SENSITIVE_CREATE(TPMS_SENSITIVE_CREATE _sensitive)
    {
        sensitive = _sensitive;
    }
    /**
    * This structure contains the sensitive creation data in a sized buffer. This structure is defined so that both the userAuth and data values of the TPMS_SENSITIVE_CREATE may be passed as a single parameter for parameter encryption purposes.
    */
    public TPM2B_SENSITIVE_CREATE() {};
    /**
    * size of sensitive in octets (may not be zero) NOTE The userAuth and data parameters in this buffer may both be zero length but the minimum size of this parameter will be the sum of the size fields of the two parameters of the TPMS_SENSITIVE_CREATE.
    */
    // private short size;
    /**
    * data to be sealed or a symmetric key value.
    */
    public TPMS_SENSITIVE_CREATE sensitive;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((sensitive!=null)?sensitive.toTpm().length:0, 2);
        if(sensitive!=null)
            sensitive.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _size));
        sensitive = TPMS_SENSITIVE_CREATE.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2B_SENSITIVE_CREATE fromTpm (byte[] x) 
    {
        TPM2B_SENSITIVE_CREATE ret = new TPM2B_SENSITIVE_CREATE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_SENSITIVE_CREATE fromTpm (InByteBuf buf) 
    {
        TPM2B_SENSITIVE_CREATE ret = new TPM2B_SENSITIVE_CREATE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_SENSITIVE_CREATE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_SENSITIVE_CREATE", "sensitive", sensitive);
    };
    
    
};

//<<<

