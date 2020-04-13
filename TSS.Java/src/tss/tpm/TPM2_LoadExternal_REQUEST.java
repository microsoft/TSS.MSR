package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command is used to load an object that is not a Protected Object into the TPM. The
 *  command allows loading of a public area or both a public and sensitive area.
 */
public class TPM2_LoadExternal_REQUEST extends TpmStructure
{
    /** the sensitive portion of the object (optional) */
    public TPMT_SENSITIVE inPrivate;
    
    /** the public portion of the object */
    public TPMT_PUBLIC inPublic;
    
    /** hierarchy with which the object area is associated */
    public TPM_HANDLE hierarchy;
    
    public TPM2_LoadExternal_REQUEST() {}
    
    /**
     *  @param _inPrivate the sensitive portion of the object (optional)
     *  @param _inPublic the public portion of the object
     *  @param _hierarchy hierarchy with which the object area is associated
     */
    public TPM2_LoadExternal_REQUEST(TPMT_SENSITIVE _inPrivate, TPMT_PUBLIC _inPublic, TPM_HANDLE _hierarchy)
    {
        inPrivate = _inPrivate;
        inPublic = _inPublic;
        hierarchy = _hierarchy;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(inPrivate != null ? inPrivate.toTpm().length : 0, 2);
        if (inPrivate != null)
            inPrivate.toTpm(buf);
        buf.writeInt(inPublic != null ? inPublic.toTpm().length : 0, 2);
        if (inPublic != null)
            inPublic.toTpm(buf);
        hierarchy.toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _inPrivateSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inPrivateSize));
        inPrivate = TPMT_SENSITIVE.fromTpm(buf);
        buf.structSize.pop();
        int _inPublicSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inPublicSize));
        inPublic = TPMT_PUBLIC.fromTpm(buf);
        buf.structSize.pop();
        hierarchy = TPM_HANDLE.fromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_LoadExternal_REQUEST fromTpm (byte[] x) 
    {
        TPM2_LoadExternal_REQUEST ret = new TPM2_LoadExternal_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_LoadExternal_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_LoadExternal_REQUEST ret = new TPM2_LoadExternal_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_LoadExternal_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_SENSITIVE", "inPrivate", inPrivate);
        _p.add(d, "TPMT_PUBLIC", "inPublic", inPublic);
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
    }
}

//<<<

