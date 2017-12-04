package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to load objects into the TPM. This command is used when both a TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be loaded, the TPM2_LoadExternal command is used.
*/
public class TPM2_Load_REQUEST extends TpmStructure
{
    /**
     * This command is used to load objects into the TPM. This command is used when both a TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be loaded, the TPM2_LoadExternal command is used.
     * 
     * @param _parentHandle TPM handle of parent key; shall not be a reserved handle Auth Index: 1 Auth Role: USER 
     * @param _inPrivate the private portion of the object 
     * @param _inPublic the public portion of the object
     */
    public TPM2_Load_REQUEST(TPM_HANDLE _parentHandle,TPM2B_PRIVATE _inPrivate,TPMT_PUBLIC _inPublic)
    {
        parentHandle = _parentHandle;
        inPrivate = _inPrivate;
        inPublic = _inPublic;
    }
    /**
    * This command is used to load objects into the TPM. This command is used when both a TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be loaded, the TPM2_LoadExternal command is used.
    */
    public TPM2_Load_REQUEST() {};
    /**
    * TPM handle of parent key; shall not be a reserved handle Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE parentHandle;
    /**
    * the private portion of the object
    */
    public TPM2B_PRIVATE inPrivate;
    /**
    * size of publicArea NOTE The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.
    */
    // private short inPublicSize;
    /**
    * the public portion of the object
    */
    public TPMT_PUBLIC inPublic;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        parentHandle.toTpm(buf);
        inPrivate.toTpm(buf);
        buf.writeInt((inPublic!=null)?inPublic.toTpm().length:0, 2);
        if(inPublic!=null)
            inPublic.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        parentHandle = TPM_HANDLE.fromTpm(buf);
        inPrivate = TPM2B_PRIVATE.fromTpm(buf);
        int _inPublicSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inPublicSize));
        inPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_Load_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Load_REQUEST ret = new TPM2_Load_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_Load_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Load_REQUEST ret = new TPM2_Load_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Load_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "parentHandle", parentHandle);
        _p.add(d, "TPM2B_PRIVATE", "inPrivate", inPrivate);
        _p.add(d, "TPMT_PUBLIC", "inPublic", inPublic);
    };
    
    
};

//<<<

