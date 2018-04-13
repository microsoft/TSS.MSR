package tss;
import java.util.*;

import tss.tpm.*;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * @author pengland
 *
 */
/**
 * @author pengland
 *
 */
public class Helpers {

	public static byte[] hostToNet(long x)
	{
        byte[] bufx = new byte[8];
        ByteBuffer buf = ByteBuffer.wrap(bufx);
        return buf.putLong(x).array();
	}
	public static byte[] hostToNet(int x)
	{
        byte[] bufx = new byte[4];
        ByteBuffer buf = ByteBuffer.wrap(bufx);
        return buf.putInt(x).array();
	}
	public static byte[] hostToNet(short x)
	{
        byte[] bufx = new byte[2];
        ByteBuffer buf = ByteBuffer.wrap(bufx);
        return buf.putShort(x).array();
	}
	public static int netToHost(byte[] x) 
	{
		ByteBuffer b = ByteBuffer.wrap(x);
		int val = b.getInt();
		return val;
	}
	public static String toHex(byte[] x)
	{
		int count=0;
		StringBuilder sb = new StringBuilder(x.length * 2);
		for (byte b: x)
		{
			sb.append(String.format("%02x", b));
			if (count++ %4==3)
				sb.append(" ");
		}
		sb.append("(" + x.length +" bytes)");
		return sb.toString();
	}
	public static byte[] fromHex(String _s)
	{
		// allow a bit of formatting to improve source readability
		String s = _s.replace(" ", "");
		s = s.replace("_", "");
		
		if (s.length() % 2 !=0 )
		{
			throw new RuntimeException("string must have an even number of characters");
		}
		byte[] x = new byte[s.length()/2];
		for (int j=0;j<s.length();j+=2)
		{
			 x[j/2] = (byte)((Character.digit(s.charAt(j), 16) << 4) + 
					 		 Character.digit(s.charAt(j+1), 16));
		}
		return x;
	}
	
	public static String toHex(byte[] x, int start, int len)
	{
		StringBuilder sb = new StringBuilder(len * 2);
		for (int j=start; j<start+len; j++)
		{
			sb.append(String.format("%02x", x[j]));
		}
		return sb.toString();
	}
	
	static Random rand;
	
	public static byte[] getRandom(int numBytes)
	{
		if (rand==null)
			rand = new Random();
		
		byte[] res = new byte[numBytes];
		rand.nextBytes(res);
		return res;
	}
	
	public static String arrayToString(Object arrX)
	{
		if (arrX instanceof byte[])
		{
			byte[] arr = (byte[]) arrX;
			return toHex(arr);
		}
		assert(false);
		return "";
	}
	
	public static void nonDefaultMarshallOut(OutByteBuf buf, TpmStructure s)
	{
		if (s instanceof TPMT_SYM_DEF_OBJECT)
		{
			TPMT_SYM_DEF_OBJECT sdo = (TPMT_SYM_DEF_OBJECT) s;
			sdo.algorithm.toTpm(buf);
			if(sdo.algorithm == TPM_ALG_ID.NULL)return;
			buf.writeInt(sdo.keyBits, 2);
			sdo.mode.toTpm(buf);
		}
		else if (s instanceof TPMT_SYM_DEF)
		{
			TPMT_SYM_DEF sd = (TPMT_SYM_DEF) s;
			sd.algorithm.toTpm(buf);
			if(sd.algorithm == TPM_ALG_ID.NULL)return;
			buf.writeInt(sd.keyBits, 2);
			sd.mode.toTpm(buf);
		}
		else
			throw new AssertionError("nonDefaultMarshallOut: unexpected TPM structure type");
	}
	
	public static void nonDefaultMarshallIn(InByteBuf buf, TpmStructure s)
	{
		if (s instanceof TPMT_SYM_DEF_OBJECT)
		{
			TPMT_SYM_DEF_OBJECT sdo = (TPMT_SYM_DEF_OBJECT) s;
			sdo.algorithm = TPM_ALG_ID.fromTpm(buf);
			if(sdo.algorithm == TPM_ALG_ID.NULL)return;
			sdo.keyBits = (short) buf.readInt(2);
			sdo.mode = TPM_ALG_ID.fromTpm(buf);
		}
		else if (s instanceof TPMT_SYM_DEF)
		{
			TPMT_SYM_DEF sd = (TPMT_SYM_DEF) s;
			sd.algorithm = TPM_ALG_ID.fromTpm(buf);
			if(sd.algorithm == TPM_ALG_ID.NULL)return;
			sd.keyBits = (short) buf.readInt(2);
			sd.mode = TPM_ALG_ID.fromTpm(buf);
		}
		else
			throw new AssertionError("should not be here");
	}
	
	public static byte[] concatenate(byte[][] a)
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
	    for (byte[] b : a)
	        os.write(b, 0, b.length);
	    return os.toByteArray();
	}
	
	public static byte[] concatenate(byte[] a, byte[] b)
	{
		return concatenate(new byte[][] {a,b});
	}

	public static byte[] concatenate(byte[] a, byte[] b, byte[] c)
	{
		return concatenate(new byte[][] {a,b,c});
	}
	
    private static byte[] shiftRightInternal(byte[] x, int numBits)
    {
        if (numBits > 7)
        {
            throw new RuntimeException("ShiftRightInternal: Can only shift up to 7 bits");
        }
        int numCarryBits = 8 - numBits;
        byte[] y = new byte[x.length];
        for (int j = x.length - 1; j >= 0; j--)
        {
            y[j] = (byte)(x[j] >> numBits);
            if (j != 0)
            {
                y[j] |= (byte)(x[j - 1] << numCarryBits);
            }
        }
        return y;
    }
    
    public static byte[] shiftRight(byte[] x, int numBits)
    {
    	byte[] y = new byte[x.length - numBits/8];

        for (int j = 0; j < y.length; j++) {
            y[j] = x[j];
        }
        return shiftRightInternal(y, numBits % 8);
    }

	public static boolean byteArraysEqual(byte[] a, byte[] b)
	{
		if(a.length!=b.length)return false;
		for(int j=0;j<a.length;j++)
		{
			if(a[j]!=b[j])return false;
		}
		return true;
	}

	public static byte[] byteArrayToLenPrependedByteArray(byte[] x)
	{
		return (new TPM2B_DATA(x)).toTpm();
	}
	
	public static byte[] clone(byte[] in)
	{
		return in.clone();
	}
	
	@SafeVarargs
	public static <T extends Object> boolean isOneOf(T val, T... values)
	{
		if (values == null)
			return val == null;
		for (T v: values)
		{
			if (val == v)
				return true;
		}
		return false;
	}
	
}
