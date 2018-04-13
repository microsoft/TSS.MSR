package tss;
import java.io.IOException;
import java.net.Socket;

public class TpmDeviceTcp extends TpmDeviceBase 
{
	protected Socket CommandSocket = null;
	protected Socket SignalSocket = null;
	boolean linuxTrm;
	boolean oldTrm;
	
	boolean responsePending;
	int currentLocality;
	
	public TpmDeviceTcp(String hostName, int port)
	{
		this.linuxTrm = false;
		connect(hostName, port);
	}
	
	//public interface DummyTrmLibrary extends Library {}
	
	public TpmDeviceTcp(String hostName, int port, boolean linuxTrm)
	{
		this.linuxTrm = linuxTrm;
		oldTrm = true;
		connect(hostName, port);
	}
	
	private void connect(String hostName, int port)
	{
		try {
			CommandSocket = new Socket(hostName, port);
			if (!linuxTrm)
				SignalSocket = new Socket(hostName, port+1);
		} catch (Exception e) {
			if (CommandSocket != null)
				try { CommandSocket.close(); } catch (IOException ioe) {}
			throw new TpmException("Failed to connect to the TPM at " + hostName + ":" + 
									Integer.toString(port) + "/" + Integer.toString(port+1), e);
		}
		
		if (linuxTrm)
		{
			byte[] cmdGetRandom = new byte[]{
	                (byte)0x80, 0x01,             // TPM_ST_NO_SESSIONS
	                0, 0, 0, 0x0C,          // length
	                0, 0, 0x01, 0x7B,       // TPM_CC_GetRandom
	                0, 0x08                 // Command parameter - num random bytes to generate
	        };

	        byte[] resp = null;
	        try
	        {
	            dispatchCommand(cmdGetRandom);
	            resp = getResponse();
	        }
	        catch (Exception e) {}
	        if (resp == null || resp.length != 20)
	        {
	        	try { CommandSocket.close(); } catch (IOException ioe) {}
	            if (oldTrm)
	            {
	                oldTrm = false;
		        	//System.out.println("==>> Trying to connect using new protocol");
	                connect(hostName, port);
	            }
	            else
	                throw new TpmException("Unknown user mode TRM protocol version");
	        }
	        //System.out.println("==>> Connected to " + (oldTrm ? "OLD TRM" : "NEW TRM"));
		}
	}
	
	@Override
	public void dispatchCommand(byte[] commandBuffer) 
	{
		writeInt(CommandSocket, TcpTpmCommands.SendCommand.Val);
		writeBuf(CommandSocket, new byte[] {(byte) currentLocality});
		if (linuxTrm && oldTrm)
		{
			// Send 'debugMsgLevel'
			writeBuf(CommandSocket, new byte[]{0});
			// Send 'commandSent' status bit
			writeBuf(CommandSocket, new byte[]{1});
		}	
		writeInt(CommandSocket, commandBuffer.length);
		try {
			CommandSocket.getOutputStream().write(commandBuffer);
			responsePending = true;
		} catch (IOException e) {
			throw new TpmException("Error sending data to the TPM", e);
		}
	}
	
	@Override
	public byte[] getResponse()
	{
		if(!responsePending)
		{
			throw new TpmException("Cannot getResponse() without a prior dispatchCommand()");
		}
		responsePending = false;
		byte[] outBuf = readEncapsulated(CommandSocket);
		readInt(CommandSocket);
		return outBuf;
	}
	
	@Override
	public boolean responseReady()
	{
		if(!responsePending)
		{
			throw new TpmException("Cannot responseReady() without a prior dispatchCommand()");
		}
		int available;
		try {
			available = CommandSocket.getInputStream().available();
		} catch (IOException e) {
			throw new TpmException("Error getting data from the TPM", e);
		}
		return (available>0);
	}
	
    @Override
	public void powerCycle() 
    {
        powerOff();
        powerOn();
    }

    @Override
	public void powerOff() 
    {
        sendCmdAndGetAck(SignalSocket, TcpTpmCommands.SignalPowerOff);
        sendCmdAndGetAck(SignalSocket, TcpTpmCommands.SignalNvOff);
    }

    @Override
	public void powerOn() 
    {
        sendCmdAndGetAck(SignalSocket, TcpTpmCommands.SignalPowerOn);
        sendCmdAndGetAck(SignalSocket, TcpTpmCommands.SignalNvOn);
    }

    @Override
	public void setLocality(int locality)  
    {
    	currentLocality = locality;
    }
    
    public void sendCmdAndGetAck(Socket s, TcpTpmCommands comm) 
	{
		writeEncapsulated(s, Helpers.hostToNet(comm.getVal()));
		getAck(s);
	}
    private void getAck(Socket s) 
    {
        readInt(s);
    }
    private int readInt(Socket s) 
    {
        int val=-1;
		try {
			val = Helpers.netToHost(readBuf(s, 4));
		} catch (Exception e) {
			throw new TpmException("TPM IO error", e);
		}
        return val;
    }

    private void writeInt(Socket s, int val) 
    {
    	writeBuf(s, Helpers.hostToNet(val));
    }
	
    private void writeBuf(Socket s, byte[] buffer) 
	{
		try 
		{
			s.getOutputStream().write(buffer, 0, buffer.length);
		} catch (IOException e) {
			throw new TpmException("TPM IO error", e);
		}
	}
	
	private byte[] readBuf(Socket s, int numBytes) 
	{
		byte[] buf = new byte[numBytes];
		int numRead = 0;
		while(numRead<numBytes)
		{
			int sz;
			try {
				sz = s.getInputStream().read(buf, numRead, numBytes-numRead);
			} catch (IOException e) {
				throw new TpmException("TPM IO error", e);
			}
			numRead+=sz;
		}
		return buf;
	}
	
	private void writeEncapsulated(Socket s, byte[] buf) 
	{
		writeBuf(s, Helpers.hostToNet(buf.length));
		writeBuf(s, buf);
	}

	private byte[] readEncapsulated(Socket s) 
	{
		byte[] t = readBuf(s, 4);
		int sz = Helpers.netToHost(t);
		return readBuf(s, sz);
	}
	
	@Override
	public void close() throws IOException
	{
		if (CommandSocket != null) {
			writeInt(CommandSocket, TcpTpmCommands.SessionEnd.Val);
			CommandSocket.close();
			CommandSocket = null;
		}
		if (SignalSocket != null) {
			writeInt(SignalSocket, TcpTpmCommands.SessionEnd.Val);
			SignalSocket.close();
			SignalSocket = null;
		}
	}
	
	/**
	 * Commands of the Microsoft TPM simulator TCP protocol
	 */
	enum TcpTpmCommands
    {
        SignalPowerOn (1),
        SignalPowerOff (2),
        SignalPPOn (3),
        SignalPPOff (4),
        SignalHashStart (5),
        SignalHashData (6),
        SignalHashEnd (7),
        SendCommand (8),
        SignalCancelOn (9),
        SignalCancelOff (10),
        SignalNvOn (11),
        SignalNvOff (12),
        SignalKeyCacheOn (13),
        SignalKeyCacheOff (14),
        RemoteHandshake (15),
        //SetAlternativeResult = 16,    // Not used since 1.38h
        SessionEnd (20),
        Stop (21),
        TestFailureMode (30);
        
        private int Val;
		TcpTpmCommands(int val)
		{
			setVal(val);
		}
		public int getVal() {
			return Val;
		}
		public void setVal(int val) {
			Val = val;
		}
    } // enum TcpTpmCommands

	/*
	public static TpmDeviceTcp Test() throws Exception
	{
		TpmDeviceTcp d = new TpmDeviceTcp("localhost", 2321);
		d.powerCycle();
		byte[] startup = new byte[] 
				{
				(byte) 0x80,1,			// sessions tag
				0,0,0,(byte) 0x0c,		// length (including header)
				0,0,1, (byte) 0x44,		// Startup
				0,0						// SU_CLEAR
				};
				
		d.dispatchCommand(startup);
		byte[] zz = d.getResponse();
		System.out.println("Got header" + Helpers.ToHex(zz) + " bytes");

		byte[] getRandom = new byte[] 
				{
				(byte) 0x80,1,			// sessions tag
				0,0,0,(byte) 0x0c,		// length (including header)
				0,0,1, (byte) 0x7b,		// GetRandom
				0,0x14					// 20 bytes
				};
		
		d.dispatchCommand(getRandom);
		byte[] data = d.getResponse();
		
		InByteBuf respBuf = new InByteBuf(data);
		// get the standard header
		TPM_ST respTag = TPM_ST.fromInt(respBuf.readInt(2));
		int respSize = respBuf.readInt(4);
		TPM_RC responseCode = TPM_RC.fromInt(respBuf.readInt(4));
		
		System.out.println("Got header" + Helpers.ToHex(data, 0, 10) + " bytes");
		System.out.println("Got data" + Helpers.ToHex(data, 10, 20) + " bytes");

		
		return d;
	}
	*/
}
