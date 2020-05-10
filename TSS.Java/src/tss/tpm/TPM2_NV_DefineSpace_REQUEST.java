package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command defines the attributes of an NV Index and causes the TPM to reserve space to
 *  hold the data associated with the NV Index. If a definition already exists at the NV Index, the
 *  TPM will return TPM_RC_NV_DEFINED.
 */
public class TPM2_NV_DefineSpace_REQUEST extends TpmStructure
{
    /**
     *  TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** the authorization value */
    public byte[] auth;
    
    /** the public parameters of the NV area */
    public TPMS_NV_PUBLIC publicInfo;
    
    public TPM2_NV_DefineSpace_REQUEST() { authHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _authHandle TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _auth the authorization value
     *  @param _publicInfo the public parameters of the NV area
     */
    public TPM2_NV_DefineSpace_REQUEST(TPM_HANDLE _authHandle, byte[] _auth, TPMS_NV_PUBLIC _publicInfo)
    {
        authHandle = _authHandle;
        auth = _auth;
        publicInfo = _publicInfo;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
        buf.writeSizedByteBuf(auth);
        buf.writeShort(publicInfo != null ? publicInfo.toTpm().length : 0);
        if (publicInfo != null)
            publicInfo.toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
        int _authSize = buf.readShort() & 0xFFFF;
        auth = new byte[_authSize];
        buf.readArrayOfInts(auth, 1, _authSize);
        int _publicInfoSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _publicInfoSize));
        publicInfo = TPMS_NV_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_NV_DefineSpace_REQUEST fromTpm (byte[] x) 
    {
        TPM2_NV_DefineSpace_REQUEST ret = new TPM2_NV_DefineSpace_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_NV_DefineSpace_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_DefineSpace_REQUEST ret = new TPM2_NV_DefineSpace_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_DefineSpace_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "byte", "auth", auth);
        _p.add(d, "TPMS_NV_PUBLIC", "publicInfo", publicInfo);
    }
}

//<<<

