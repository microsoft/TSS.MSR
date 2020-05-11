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

	public void writeNum(long val, int numBytes) 
	{
		switch(numBytes)
		{
		case 1: s.write((byte)val); return;
		case 2: writeByteBuf(Helpers.hostToNet((short)val)); return;
		case 4: writeByteBuf(Helpers.hostToNet((int)val)); return;
		case 8: writeByteBuf(Helpers.hostToNet(val)); return;
		default: assert(false);
		}
	}

	public void writeByte(byte val) { s.write((byte)val); }

	public void writeShort(int val) { writeNum(val, 2); }

	public void writeInt(int val) { writeNum(val, 4); }

	public void writeInt64(long val) { writeNum(val, 8); }

	public void write(TpmMarshaller o)
	{
		o.toTpm(this);
	}
	
	public void writeObjArr(TpmMarshaller[] arr) 
	{
        // Length of the array size is always 4 bytes
		if (arr == null)
		{
			writeInt(0);
			return;
		}

        writeInt(arr.length);
		for(TpmMarshaller o : arr)
			o.toTpm(this);
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
