package kobae964_app.kvm3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CallStack {
	public CallStack()
	{
		data=new ArrayList<Integer>(1000);
		heap=new Heap();
	}
	private void push(int type,long val)
	{
		data.add(type);
		data.add((int)val);
		data.add((int)(val>>>32));
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
			val|=(long)array[i]<<(i*16);
		}
		push(DataType.STRING.ordinal(),val);
		
	}
	public void pushObject(Object o)
	{
		long val=heap.create(o);
		push(DataType.OBJECT.ordinal(),val);
	}
	private Map<Integer,Long> pop()
	{
		int type=data.get(data.size()-3);
		long value=data.get(data.size()-2);
		value&=0xffffffffL;
		value|=(long)data.get(data.size()-1)<<32L;
		data.subList(data.size()-3, data.size()).clear();
		return Collections.singletonMap(type, value);
	}
	public double popReal()
	{
		Map<Integer,Long> result=pop();
		Integer[] a=result.keySet().toArray(new Integer[0]);
		if(a[0]!=DataType.REAL.ordinal())
		{
			throw new IllegalStateException("REAL was required, but "+DataType.values()[a[0]]+" returned");
		}
		return Double.longBitsToDouble(result.get(a[0]));
	}
	public String popString()
	{
		Map<Integer,Long> result=pop();
		Integer[] a=result.keySet().toArray(new Integer[0]);
		if(a[0]!=DataType.STRING.ordinal()&&a[0]!=DataType.OBJECT.ordinal())
		{
			throw new IllegalStateException("STRING was required, but "+DataType.values()[a[0]]+" returned");
		}
		long value=result.get(a[0]);
		if(a[0]==DataType.OBJECT.ordinal())
		{
			return (String)heap.retrieve(value);
		}
		char[] tmp=new char[4];
		int i=0;
		for(;i<4&&value!=0;i++)
		{
			tmp[i]=(char)value;
			value>>>=16;
		}
		return new String(tmp).substring(0, i);
	}
	private List<Integer> data;
	private Heap heap;
	static enum DataType
	{
		REAL,
		INT,
		BOOL,
		STRING,
		OBJECT,
	}
	@Override
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		sb.append('[');
		for(int i:data)
		{
			sb.append(String.format("%08x, ",i));
		}
		sb.append(']');
		return sb.toString();
	}
	public static void main(String [] args)
	{
		CallStack cs=new CallStack();
		cs.pushString("xfyxfxg");
		cs.pushReal(3.0);
		System.out.println(cs);
		System.out.println("real:"+cs.popReal());
		String v=cs.popString();
		System.out.println("string:"+v+", len="+v.length());
	}
}
