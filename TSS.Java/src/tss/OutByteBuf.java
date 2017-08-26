package tss;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class OutByteBuf {
	ByteArrayOutputStream s;
	public OutByteBuf()
	{
		s = new ByteArrayOutputStream();
	}
	public void reset()
	{
		s.reset();
	}
	public void write(byte x) 
	{
		s.write(x);
	}
	public void write(short x) 
	{
		try
		{
			s.write(Helpers.hostToNet(x));
		}
		catch(IOException e)
		{
			throw new RuntimeException();
		}
	}
	public void write(int x) 
	{
		try
		{
			s.write(Helpers.hostToNet(x));
		} 
		catch(IOException e)
		{
			throw new RuntimeException();
		}
		
	}
	public void write(long x) 
	{
		try
		{
			s.write(Helpers.hostToNet(x));
		} 
		catch(IOException e)
		{
			throw new RuntimeException();
		}
	}
	public void write(byte[] x) 
	{
		if(x==null)return;
		s.write(x, 0, x.length);
	}
	public void writeInt(int val, int numBytes) 
	{
		switch(numBytes)
		{
		case 1: write((byte) val);
		return;
		case 2: write((short) val);
		return;
		case 4: write(val);
		return;
		default:
			assert(false);
		}
	}
	public void writeLong(long val)
	{
		write(val);
	}
	public void write(TpmMarshaller o)
	{
		o.toTpm(this);
	}
	
	public void writeArrayOfTpmObjects(Object arr) 
	{
		if(arr==null)return;
		Object[] objs = (Object[]) arr;
		
		for(Object o : objs)
		{
			TpmStructure xx = (TpmStructure) o;
			try
			{
				xx.toTpm(this);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
	public static byte[] arrayToByteBuf(TpmStructure[] arr)
	{
		OutByteBuf buf = new OutByteBuf();
		for(Object o : arr)
		{
			TpmStructure xx = (TpmStructure) o;
			try
			{
				xx.toTpm(buf);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				throw new AssertionError("Marshalling error");
			}
		}
		return buf.getBuf();
	}

	public void writeArray(byte[] x) 
	{
		if(x==null)return;
		write(x);
	}
	public void writeArray(short[] x) 
	{
		if(x==null)return;
		for(int j=0;j<x.length;j++)write(x[j]);
	}
	public void writeArray(int[] x) 
	{
		if(x==null)return;
		for(int j=0;j<x.length;j++)write(x[j]);
	}
	public void writeArrayFragment(byte[] x, int start, int end) 
	{
		if(x==null)return;
		
		for(int j=start;j<end;j++)
		{
			write(x[j]);
		}
	}


	
	public void writeArrayOfIntTypes(Object[] objs, int intSize) 
	{
		return;
	}

	
	public byte[] getBuf()
	{
		return s.toByteArray();
	}
	
	public int size()
	{
		return s.size();
	}
	
}
