package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to advance the value of the TPMs Clock. The command will fail if newTime is less than the current value of Clock or if the new time is greater than FFFF00000000000016. If both of these checks succeed, Clock is set to newTime. If either of these checks fails, the TPM shall return TPM_RC_VALUE and make no change to Clock.
*/
public class TPM2_ClockSet_REQUEST extends TpmStructure
{
    /**
     * This command is used to advance the value of the TPMs Clock. The command will fail if newTime is less than the current value of Clock or if the new time is greater than FFFF00000000000016. If both of these checks succeed, Clock is set to newTime. If either of these checks fails, the TPM shall return TPM_RC_VALUE and make no change to Clock.
     * 
     * @param _auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER 
     * @param _newTime new Clock setting in milliseconds
     */
    public TPM2_ClockSet_REQUEST(TPM_HANDLE _auth,long _newTime)
    {
        auth = _auth;
        newTime = _newTime;
    }
    /**
    * This command is used to advance the value of the TPMs Clock. The command will fail if newTime is less than the current value of Clock or if the new time is greater than FFFF00000000000016. If both of these checks succeed, Clock is set to newTime. If either of these checks fails, the TPM shall return TPM_RC_VALUE and make no change to Clock.
    */
    public TPM2_ClockSet_REQUEST() {};
    /**
    * TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER
    */
    public TPM_HANDLE auth;
    /**
    * new Clock setting in milliseconds
    */
    public long newTime;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        auth.toTpm(buf);
        buf.write(newTime);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        auth = TPM_HANDLE.fromTpm(buf);
        newTime = buf.readLong();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ClockSet_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ClockSet_REQUEST ret = new TPM2_ClockSet_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ClockSet_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ClockSet_REQUEST ret = new TPM2_ClockSet_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ClockSet_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "auth", auth);
        _p.add(d, "ulong", "newTime", newTime);
    };
    
    
};

//<<<

