package tss;
import java.io.*;
import java.util.Arrays;


public class TpmDeviceLinux extends TpmDevice
{
    RandomAccessFile devTpm = null;
    int respSize = 0;
    byte[] respBuf = null;


    public TpmDeviceLinux() {}

    @Override
    public boolean connect()
    {
        if (devTpm != null)
            return true;

        String errorRM = openTpmDevice("/dev/tpmrm0");
        if (errorRM != null)
        {
            String errorTPM = openTpmDevice("/dev/tpm0");
            if (errorTPM != null)
            {
                System.err.println("TSS.Java: " + errorRM + " and " + errorTPM);
                return false;
                //throw new RuntimeException("TSS.Java: " + errorRM + " and " + errorTPM);
            }
            //System.out.println("Connected to system TPM");
        }
        //else System.out.println("Connected to kernel mode TRM");
        respSize = 0;
        respBuf = new byte[4096];
        return true;
    }

    @Override
    public void close()
    {
        if (devTpm != null)
            try { devTpm.close(); } catch (IOException ioe) {}
    }
    
    private String openTpmDevice(String devName)
    {
        File devTpm0 = new File(devName);
        if (!devTpm0.exists())
            return devName + " does not exist";
        try {
            devTpm = new RandomAccessFile(devName, "rwd");
        } catch (Exception e) {
            return "Failed to open " + devName + " in RW mode";
        }
        return null;
    }

    @Override
    public void dispatchCommand(byte[] command)
    {
        try {
            devTpm.write(command);
        } catch (IOException e) {
            throw new RuntimeException("TSS.Java fatal error: Failed to send TPM command to /dev/tpm0");
        }
        
        try {
            int count = 0;
            do {
                respSize = devTpm.read(respBuf);
                if (respSize > 0)
                    break;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {}
            } while (count++ < 20);
        } catch (IOException e) {
            throw new RuntimeException("TSS.Java fatal error: Failed to read TPM response from /dev/tpm0");
        }
        if (respSize <= 0)
            throw new RuntimeException("TSS.Java fatal error: No response from /dev/tpm0");
    }

    @Override
    public byte[] getResponse() {
        if(respSize == 0) 
            throw new TpmException("No previous TPM command, or TPM command failed");
        byte[] resp = Arrays.copyOf(respBuf, respSize);
        respSize = 0;
        return resp;
    }

    @Override
    public boolean responseReady() 
    {
        // TBS is blocking
        return true;
    }    
}
