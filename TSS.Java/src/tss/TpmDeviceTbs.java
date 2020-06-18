package tss;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

import tss.tpm.TPM_RC;


public class TpmDeviceTbs extends TpmDevice
{
    int tbsHandle = -1;
    byte[] lastTpmResponse = null;
    

    public interface TBSLibrary extends StdCallLibrary 
    {
        TBSLibrary INSTANCE = Native.loadLibrary("TBS", TBSLibrary.class);
        
        public static class TBS_CONTEXT_PARAMS2 extends Structure
        {
            public int version;
            public int params;

            @Override
            protected List<String> getFieldOrder() 
            {
                return Arrays.asList(new String[] { "version", "params"});
            }
        }

        int Tbsi_Context_Create(TBS_CONTEXT_PARAMS2 params, IntByReference returnedHandle);
        int Tbsip_Context_Close(int handle);
        int Tbsip_Submit_Command(int handle, int locality, int priority,
                                byte[] inBuf, int inBufLen, 
                                PointerByReference outBuf, IntByReference outBufLen); 
    }


    public TpmDeviceTbs() {}

    @Override
    public boolean connect()
    {
        tbsHandle = -1;
        TBSLibrary.TBS_CONTEXT_PARAMS2 parms = new TBSLibrary.TBS_CONTEXT_PARAMS2();
        parms.version = 2;
        parms.params = 1 << 2;
    
        IntByReference handleRef = new IntByReference();
        handleRef.setValue(333);

        int res = TBSLibrary.INSTANCE.Tbsi_Context_Create(parms, handleRef);
        if (res != 0)
        {
            System.err.println("TSS.Java; Tbsi_Context_Create failed with error:" + new TPM_RC(res).toStringVerbose());
            return false;
        }
        tbsHandle = handleRef.getValue();
        return true;
    }

    @Override
    public void close()
     {
        // todo: Auto-generated method stub
        TBSLibrary.INSTANCE.Tbsip_Context_Close(tbsHandle);
    }

    @Override
    public void dispatchCommand(byte[] command) 
    {
        Memory buf = new Memory(4096);
        PointerByReference response = new PointerByReference();
        response.setPointer(buf);
        
        IntByReference responseLen = new IntByReference();
        responseLen.setValue(4096);
        
        int res = TBSLibrary.INSTANCE.Tbsip_Submit_Command(tbsHandle, 0,  0,  command, command.length, response, responseLen);
        if(res!=0)
        {
            throw new TpmException("Tbsip_Submit_Command failed.  Error code is:" + Integer.toHexString(res));
        }    
        int numBytesReturned = responseLen.getValue();
        lastTpmResponse = new byte[numBytesReturned];
        buf.read(0, lastTpmResponse, 0, numBytesReturned);
    }

    @Override
    public byte[] getResponse() {
        if(lastTpmResponse==null) 
        {
            throw new TpmException("No previous TPM command, or TPM command failed");
        }
        byte[] ret = lastTpmResponse.clone();
        lastTpmResponse = null;
        return ret;
    }

    @Override
    public boolean responseReady() 
    {
        // TBS is blocking
        return true;
    }    
}
