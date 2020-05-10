package tss;

import java.io.ByteArrayOutputStream;

public class OutByteBuf
 {
	ByteArrayOutputStream s;

	public OutByteBuf()	{ s = new ByteArrayOutputStream(); }

	public void reset()	{ s.reset(); }

	public void writeSizedByteBuf(byte[] x, int sizeLen)
	{
        writeNum(x != null ? x.length : 0, sizeLen);
        if (x != null)
			s.write(x, 0, x.length);
	}
	public void writeSizedByteBuf(byte[] x)	{ writeSizedByteBuf(x, 2);	}

	public void writeByteBuf(byte[] x) 
	{
		if (x != null)
			s.write(x, 0, x.length);
	}

	public void writeByte(byte val) { s.write(val); }

	public void writeShort(int val) { writeByteBuf(Helpers.hostToNet((short)val)); }

	public void writeInt(int val) { writeByteBuf(Helpers.hostToNet(val)); }

	public void writeInt64(long val) { writeByteBuf(Helpers.hostToNet(val)); }

	public void writeNum(long val, int numBytes) 
	{
		switch(numBytes)
		{
		case 1: writeByte((byte) val); return;
		case 2: writeShort((short) val); return;
		case 4: writeInt((int)val); return;
		case 8: writeInt64(val); return;
		default: assert(false);
		}
	}

	public void write(TpmMarshaller o)
	{
		o.toTpm(this);
	}
	
	public void writeObjArr(Object arr) 
	{
        // Length of the array size is always 4 bytes
		if (arr == null)
		{
			writeInt(0);
			return;
		}

		TpmStructure[] sArr = (TpmStructure[])arr;
        writeInt(sArr.length);
		for(TpmStructure s : sArr)
			s.toTpm(this);
	}

	public static byte[] arrayToByteBuf(TpmStructure[] arr)
	{
		OutByteBuf buf = new OutByteBuf();
		for(TpmStructure s : arr)
			s.toTpm(buf);
		return buf.getBuf();
	}

	public void writeArrayFragment(byte[] x, int start, int end) 
	{
		if (x == null) return;
		for(int j=start; j<end; j++)
			writeByte(x[j]);
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
