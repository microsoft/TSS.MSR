package tss;

import java.util.Map;

public abstract class TpmAttribute<T extends TpmAttribute<T>> extends TpmEnum<T> {

	protected TpmAttribute (int value, Enum<?> nameAsEnum, ValueMap<T> values)
	{
		super(value, nameAsEnum, values);
	}
	
	@SafeVarargs
	protected TpmAttribute (ValueMap<T> values, T ... attrs)
	{
		super(0, null, null);
		for (T a: attrs)
		{
			Value |= a.Value;
		}
		updateName(values);
	}

	protected TpmAttribute(int value, ValueMap<T> values)
	{
		super(value, values);
		Name = null;
		updateName(values);
	}

	// Needed to hide super class implementation
	protected static <T extends TpmEnum<T>> T fromInt (int value, ValueMap<T> values, Class<T> cls)
	{
		return null;
	}
	
	protected static <T extends TpmAttribute<T>> T attrFromInt (int value, ValueMap<T> values, Class<T> cls)
	{
		T newAttr = TpmEnum.fromInt(value, values, cls);
		((TpmAttribute<T>)newAttr).updateName(values);
		return newAttr;
	}
	
	protected boolean hasAttr(TpmAttribute<T> attr)
	{
		return attr.Value == (Value & attr.Value);
	}

	protected T maskAttr(T attr, ValueMap<T> values, Class<T> cls)
	{
		return attrFromInt(Value & attr.Value, values, cls);
	}

	private void updateName(String attrName)
	{
		if (Name == null)
			Name = attrName;
		else
			Name += " | " + attrName;
	}

	private void updateName(ValueMap<T> values)
	{
		int matchedAttrs = 0;
		for (Map.Entry<Integer, T> pair : values.entrySet())
		{
			int key = (int)pair.getKey();
			if ((Value & key) == 0)
				continue;
			
			matchedAttrs |= key;
			updateName((pair.getValue()).Name);
		}
		
		int unmatchedAttrs = Value ^ matchedAttrs;
		if (unmatchedAttrs != 0)
		{
			updateName(Integer.toHexString(unmatchedAttrs));
		}
	}

}
