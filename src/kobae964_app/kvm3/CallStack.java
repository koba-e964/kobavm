package kobae964_app.kvm3;

import java.util.ArrayList;
import java.util.List;

public class CallStack {
	public CallStack()
	{
		data=new ArrayList<Integer>(1000);
	}
	private void push(int type,long val)
	{
		data.add(type);
		data.add((int)val);
		data.add((int)(val>>>32));
		DataType.REAL.ordinal();
	}
	public void pushReal(double value)
	{
		push(DataType.REAL.ordinal(),Double.doubleToLongBits(value));
	}
	public void pushInt(long value)
	{
		push(DataType.INT.ordinal(),value);
	}
	public void pushBool(boolean value)
	{
		push(DataType.BOOL.ordinal(),value?1:0);
	}
	public void pushString(String value)
	{
		char[] array=value.toCharArray();
		if(array.length>=5)
		{
			pushObject(value);
			return;
		}
		long val=0;
		for(int i=0;i<array.length;i++)
		{
			val|=array[i]<<(16L*i);
		}
		push(DataType.STRING.ordinal(),val);
		
	}
	public void pushObject(Object o)
	{
		throw new RuntimeException();
	}
	private List<Integer> data;
	static enum DataType
	{
		REAL,
		INT,
		BOOL,
		STRING,
		OBJECT,
	}
}
