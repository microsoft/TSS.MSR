package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command creates an object and loads it in the TPM. This command allows creation of any type of object (Primary, Ordinary, or Derived) depending on the type of parentHandle. If parentHandle references a Primary Seed, then a Primary Object is created; if parentHandle references a Storage Parent, then an Ordinary Object is created; and if parentHandle references a Derivation Parent, then a Derived Object is generated.
*/
public class TPM2_CreateLoaded_REQUEST extends TpmStructure
{
    /**
     * This command creates an object and loads it in the TPM. This command allows creation of any type of object (Primary, Ordinary, or Derived) depending on the type of parentHandle. If parentHandle references a Primary Seed, then a Primary Object is created; if parentHandle references a Storage Parent, then an Ordinary Object is created; and if parentHandle references a Derivation Parent, then a Derived Object is generated.
     * 
     * @param _parentHandle Handle of a transient storage key, a persistent storage key, TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL Auth Index: 1 Auth Role: USER 
     * @param _inSensitive the sensitive data, see TPM 2.0 Part 1 Sensitive Values 
     * @param _inPublic the public template
     */
    public TPM2_CreateLoaded_REQUEST(TPM_HANDLE _parentHandle,TPMS_SENSITIVE_CREATE _inSensitive,byte[] _inPublic)
    {
        parentHandle = _parentHandle;
        inSensitive = _inSensitive;
        inPublic = _inPublic;
    }
    /**
    * This command creates an object and loads it in the TPM. This command allows creation of any type of object (Primary, Ordinary, or Derived) depending on the type of parentHandle. If parentHandle references a Primary Seed, then a Primary Object is created; if parentHandle references a Storage Parent, then an Ordinary Object is created; and if parentHandle references a Derivation Parent, then a Derived Object is generated.
    */
    public TPM2_CreateLoaded_REQUEST() {};
    /**
    * Handle of a transient storage key, a persistent storage key, TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE parentHandle;
    /**
    * size of sensitive in octets (may not be zero) NOTE The userAuth and data parameters in this buffer may both be zero length but the minimum size of this parameter will be the sum of the size fields of the two parameters of the TPMS_SENSITIVE_CREATE.
    */
    // private short inSensitiveSize;
    /**
    * the sensitive data, see TPM 2.0 Part 1 Sensitive Values
    */
    public TPMS_SENSITIVE_CREATE inSensitive;
    /**
    * size of publicArea
    */
    // private short inPublicSize;
    /**
    * the public template
    */
    public byte[] inPublic;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        parentHandle.toTpm(buf);
        buf.writeInt((inSensitive!=null)?inSensitive.toTpm().length:0, 2);
        if(inSensitive!=null)
            inSensitive.toTpm(buf);
        buf.writeInt((inPublic!=null)?inPublic.length:0, 2);
        if(inPublic!=null)
            buf.write(inPublic);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        parentHandle = TPM_HANDLE.fromTpm(buf);
        int _inSensitiveSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inSensitiveSize));
        inSensitive = TPMS_SENSITIVE_CREATE.fromTpm(buf);
        buf.structSize.pop();
        int _inPublicSize = buf.readInt(2);
        inPublic = new byte[_inPublicSize];
        buf.readArrayOfInts(inPublic, 1, _inPublicSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_CreateLoaded_REQUEST fromTpm (byte[] x) 
    {
        TPM2_CreateLoaded_REQUEST ret = new TPM2_CreateLoaded_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_CreateLoaded_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_CreateLoaded_REQUEST ret = new TPM2_CreateLoaded_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CreateLoaded_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "parentHandle", parentHandle);
        _p.add(d, "TPMS_SENSITIVE_CREATE", "inSensitive", inSensitive);
        _p.add(d, "byte", "inPublic", inPublic);
    };
    
    
};

//<<<

