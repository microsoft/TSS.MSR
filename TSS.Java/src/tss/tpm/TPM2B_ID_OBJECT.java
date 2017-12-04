package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is an output from TPM2_MakeCredential() and is an input to TPM2_ActivateCredential().
*/
public class TPM2B_ID_OBJECT extends TpmStructure
{
    /**
     * This structure is an output from TPM2_MakeCredential() and is an input to TPM2_ActivateCredential().
     * 
     * @param _credential an encrypted credential area
     */
    public TPM2B_ID_OBJECT(TPMS_ID_OBJECT _credential)
    {
        credential = _credential;
    }
    /**
    * This structure is an output from TPM2_MakeCredential() and is an input to TPM2_ActivateCredential().
    */
    public TPM2B_ID_OBJECT() {};
    /**
    * size of the credential structure
    */
    // private short size;
    /**
    * an encrypted credential area
    */
    public TPMS_ID_OBJECT credential;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((credential!=null)?credential.toTpm().length:0, 2);
        if(credential!=null)
            credential.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _size));
        credential = TPMS_ID_OBJECT.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2B_ID_OBJECT fromTpm (byte[] x) 
    {
        TPM2B_ID_OBJECT ret = new TPM2B_ID_OBJECT();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_ID_OBJECT fromTpm (InByteBuf buf) 
    {
        TPM2B_ID_OBJECT ret = new TPM2B_ID_OBJECT();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_ID_OBJECT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ID_OBJECT", "credential", credential);
    };
    
    
};

//<<<

