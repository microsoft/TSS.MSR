package tss;

import java.nio.ByteBuffer;
import java.util.Stack;

public class InByteBuf
{
	public class SizedStructInfo
	{
		// Struct start position in the marshaled input buffer 
		public int StartPos;
		
		// Total size of the struct 
		public int Size;
		
		public SizedStructInfo(int startPos, int size) {
			StartPos = startPos;
			Size = size;
		}
	}
	
	ByteBuffer b;
	public Stack<SizedStructInfo>	structSize;
	
	public InByteBuf(byte[] buf)
	{
		b = ByteBuffer.wrap(buf);
		structSize = new Stack<SizedStructInfo>();
	}
	public byte readByte()
	{
		return b.get();
	}
	public short readShort()
	{
		return b.getShort();
	}
	public int readInt()
	{
		return b.getInt();
	}
	public long readLong()
	{
		return b.getLong();
	}
	public int readInt(int numBytes)
	{
		if(numBytes==1)
			return readByte() & 0xFF;
		if(numBytes==2)
			return readShort() & 0xFFFF;
		if(numBytes==4)
			return readInt();
		throw new RuntimeException();
	}
	public byte[] readByteArray(int numBytes) 
	{
		byte[] buf = new byte[numBytes];
		b.get(buf);
		return buf;
	}
	public void readArrayOfInts(Object arrX, int intSize, int numElems)
	{
		if(arrX instanceof byte[])
		{
			byte[] arrB = (byte[]) arrX;
			for(int j=0;j<numElems;j++)
			{
				arrB[j] = (byte) readInt(intSize);
			}
			return;
		}
		Object[] arr = (Object[])arrX;
		for(int j=0;j<numElems;j++)
		{
			arr[j] = readInt(intSize);
		}
		return;
	}
	
	public void readArrayOfTpmObjects(Object arrX, int numElems)
	{
		TpmStructure[] arr = (TpmStructure[])arrX;
		for(int j=0;j<numElems;j++)
		{
			arr[j].initFromTpm(this);
		}
		return;
	}
	
	public void readTpmObject(TpmMarshaller m)
	{
		//m.fromTpmRead(this);
	}
	
	public int curPos()
	{
		return b.position();
	}
	
	public int bytesRemaining()
	{
		return b.remaining();
	}
	
	public byte[] getRemaining()
	{
		return readByteArray(bytesRemaining()); 
	}
	
	public byte[] peekRemaining()
	{
		byte[] res = getRemaining();
		b.position(0);
		return res;
	}
	
}
