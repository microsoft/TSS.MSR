package tss;

/**
 * Provides methods to convert TPM objects to a string representation
 * @author pengland
 *
 */
public class TpmStructurePrinter 
{
	StringBuilder b;
	public TpmStructurePrinter()
	{
		b = new StringBuilder();
	}
	public TpmStructurePrinter(String typeName)
	{
		b = new StringBuilder();
		write(typeName + " =\n");
		write("{\n");
	}
	@Override
	public String toString()
	{
		return b.toString();
	}
	public void endStruct()
	{
		write("}");
	}
	
	void write(String s)
	{
		s = s.replace("\n", System.lineSeparator());
		b.append(s);
	}
	void writeLine(int nesting, String data)
	{
		//String line = typeName + "<16>" + variableName + "<30>" + value + "\n";
		String line = data;
		line = tabify(line);
		line = spaces(line, nesting) +  "\n";
		write(line);
	}
	void writeLine(int nesting, String typeName, String variableName, String value)
	{
		//String line = typeName + "<16>" + variableName + "<30>" + value + "\n";
		String line = typeName + " " + variableName + " = " + value;
		line = tabify(line);
		line = spaces(line, nesting) +  "\n";
		write(line);
	}
	
	public void add(int nesting, String typeName, String variableName, Object v)
	{
		if(v==null)
		{
			// do nothing (e.g. for SYM_DEF_OBJECT)
			return;
		}
		/*
		// SYM_DEF and SYM_DEF_OBJECT are tricky..
		if(v instanceof TPMT_SYM_DEF_OBJECT)
		{
			TPMT_SYM_DEF_OBJECT sdo = (TPMT_SYM_DEF_OBJECT) v;
			writeLine(nesting, typeName, variableName, "");
			writeLine(nesting, "{");
			writeLine(nesting+1, "TPM_ALG_ID", "algorithm", sdo.algorithm.toString());
			if(sdo.algorithm!=TPM_ALG_ID._NULL)
			{
				writeLine(nesting+1, "short", "keyBits", String.valueOf(sdo.keyBits) + " (0x" + Integer.toHexString((int) sdo.keyBits)+ ")");
				writeLine(nesting+1, "TPM_ALG_ID", "mode", sdo.mode.toString());
			}
			writeLine(nesting, "}");
		}
		*/
		
		if(v instanceof TpmStructure)
		{
			
			TpmStructure sb = (TpmStructure) v;
			writeLine(nesting, typeName, variableName, "");
			writeLine(nesting, "{");
			sb.toStringInternal(this, nesting+1);
			writeLine(nesting, "}");
			return;
		}
		if(v instanceof TpmMarshaller)
		{
			writeLine(nesting, typeName, variableName, v.toString());
			return;
		}
		if(v instanceof String)
		{
			writeLine(nesting, typeName, variableName, (String) v);
			return;
		}
		if(v instanceof Integer)
		{
			Integer vi = (Integer)v;
			writeLine(nesting, typeName, variableName, String.valueOf(vi) + " (0x" + Integer.toHexString(vi)+ ")");
			return;
		}
		if(v instanceof Long)
		{
			Long vl = (Long)v;
			writeLine(nesting, typeName, variableName, String.valueOf(vl) + " (0x" + Long.toHexString(vl)+ ")");
			return;
		}
		if(v instanceof Short)
		{
			Integer vi = ((Short)v).intValue();
			writeLine(nesting, typeName, variableName, String.valueOf(vi) + " (0x" + Integer.toHexString(vi)+ ")");
			return;
		}
		
		if(v instanceof Byte)
		{
			Integer vi = ((Byte)v).intValue();
			writeLine(nesting, typeName, variableName, String.valueOf(vi) + " (0x" + Integer.toHexString(vi)+ ")");
			return;
		}
		
		if(v instanceof byte[])
		{
			writeLine(nesting, typeName+"[]", variableName, Helpers.toHex((byte[]) v));
			return;
		}
		if(v instanceof Object[])
		{
			Object[] oArr = (Object[]) v;
			int len =oArr.length;
			add(nesting, typeName, variableName+ "[" + String.valueOf(len) + "]","");
			writeLine(nesting, "{");
			for(int j=0;j<len;j++)
			{
				add(nesting+1, "[" + String.valueOf(j)+ "]", "", oArr[j]);
			}
			writeLine(nesting, "}");
			return;
		}

// TODO: REMOVE CODE
/*		
		if(v instanceof TpmMarshaller[])
		{
			throw new AssertionError("Should not be here");
		}
		if(v instanceof EnumSet<?>)
		{
			EnumSet<?> es = (EnumSet<?>) v;
			String attrs = "";
			if(es.size()!=0)
			{
				AttributeSetPrinter as = (AttributeSetPrinter)es.toArray()[0];
				attrs = as.attributeSetString(v);
			}
			writeLine(nesting, typeName, variableName, attrs);
			return;
		}
*/		
		throw new AssertionError("Should not be here");
		//assert(false);
	}
	
	static String tabify(String s)
	{
		while(true)
		{
			int pos = s.indexOf("<");
			if(pos<0)break;
			int pos2 = s.indexOf(">");
			String tabPos = s.substring(pos+1,  pos2);
			int tab = Integer.parseInt(tabPos);
			s = s.substring(0,pos) + makeSpaces(tab-pos) + s.substring(pos2+1, s.length());
		}
		return s;
	}
	static String spaces(String line, int num)
	{
		String spaces = new String(new char[num*4]).replace("\0", " ");
		line = spaces+line;
		// if there are embedded newlines, indent
		line = line.replace("\n", spaces + "\n");
		return line;
	}
	
	static String makeSpaces(int num)
	{
		return new String(new char[num]).replace("\0", " ");
	}

}
