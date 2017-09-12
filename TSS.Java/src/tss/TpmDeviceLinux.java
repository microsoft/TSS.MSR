package tss;
import java.io.*;
import java.util.Arrays;


public class TpmDeviceLinux extends TpmDeviceBase
{
	RandomAccessFile devTpm;
	int respSize;
	byte[] respBuf;
	
	public TpmDeviceLinux()
	{
		File devTpm0 = new File("/dev/tpm0");
		if (!devTpm0.exists())
			throw new RuntimeException("TSS.Java fatal error: /dev/tpm0 does not exist");
		try {
			devTpm = new RandomAccessFile("/dev/tpm0", "rwd");
		} catch (FileNotFoundException e1) {
			throw new RuntimeException("Failed to open /dev/tpm0 in RW mode");
		}
		respSize = 0;
		respBuf = new byte[8192];
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
	boolean responseReady() 
	{
		// TBS is blocking
		return true;
	}	
	
	
	@Override
	public void close() throws IOException
	{
		if (devTpm != null)
			devTpm.close();
	}
}
