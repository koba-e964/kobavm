package kobae964_app.kvm3;

import java.util.ArrayList;
import java.util.List;
import kobae964_app.kvm3.inline.KString;

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
	}
	public void push(VarEntry ve)
	{
		push(ve.type,ve.value);
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
		long addr=new KString(value).getAddress();
		pushObject(addr);
	}
	public void pushObject(int classID,byte[] data,int flags)
	{
		long val=Heap.create(classID, data, flags);
		push(DataType.OBJECT.ordinal(),val);
	}
	public void pushObject(long addr)
	{
		push(DataType.OBJECT.ordinal(),addr);
	}
	public VarEntry pop()
	{
		if(data.size()==0)
		{
			throw new IllegalStateException("Attempted to pop empty stack (in CallStack)");
		}
		int type=data.get(data.size()-3);
		long value=data.get(data.size()-2);
		value&=0xffffffffL;
		value|=(long)data.get(data.size()-1)<<32L;
		data.subList(data.size()-3, data.size()).clear();
		return new VarEntry(type, value);
	}
	public double popReal()
	{
		VarEntry result=pop();
		int a=result.type;
		if(a!=DataType.REAL.ordinal())
		{
			throw new IllegalStateException("REAL was required, but "+DataType.values()[a]+" returned");
		}
		return Double.longBitsToDouble(result.value);
	}
	public long popInt()
	{
		VarEntry result=pop();
		int a=result.type;
		if(a!=DataType.INT.ordinal())
		{
			throw new IllegalStateException("INT was required, but "+DataType.values()[a]+" returned");
		}
		return result.value;
	}
	public boolean popBool()
	{
		VarEntry result=pop();
		int a=result.type;
		if(a!=DataType.BOOL.ordinal())
		{
			throw new IllegalStateException("BOOL was required, but "+DataType.values()[a]+" returned");
		}
		return result.value!=0;
	}
	public String popString()
	{
		VarEntry result=pop();
		int a=result.type;
		if(/*a!=DataType.STRING.ordinal()&&*/a!=DataType.OBJECT.ordinal())
		{
			throw new IllegalStateException("STRING was required, but "+DataType.values()[a]+" returned");
		}
		long value=result.value;
		if(a==DataType.OBJECT.ordinal())
		{
			KVMObject ret=Heap.retrieve(value);
			return new String(ret.data);
		}
		throw new IllegalStateException();
		/*
		char[] tmp=new char[4];
		int i=0;
		for(;i<4&&value!=0;i++)
		{
			tmp[i]=(char)value;
			value>>>=16;
		}
		return new String(tmp).substring(0, i);
		*/
	}
	public KVMObject popObject()
	{
		VarEntry result=pop();
		int a=result.type;
		if(a!=DataType.OBJECT.ordinal())
		{
			throw new IllegalStateException("OBJECT was required, but "+DataType.values()[a]+" returned");
		}
		long value=result.value;
		KVMObject ret=Heap.retrieve(value);
		return ret;
	}
	private List<Integer> data;
	@Override
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		sb.append('[');
		for(int i=0,s=this.size();i<s;i++)
		{
			sb.append(getAt(i));
			sb.append(", ");
		}
		sb.append(']');
		return sb.toString();
	}
	public VarEntry getAt(int index)
	{
		int size_3=data.size()-3;
		int type=data.get(size_3-3*index);
		long value=data.get(size_3-3*index+1);
		value|=(long)data.get(size_3-3*index+2)<<32L;
		return new VarEntry(type,value);
	}
	public void setAt(int index,VarEntry ve)
	{
		int size_3=data.size()-3;
		data.set(size_3-3*index,ve.type);
		data.set(size_3-3*index+1,(int)ve.value);
		data.set(size_3-3*index+2,(int)(ve.value>>>32L));
	}
	public int size()
	{
		return data.size()/3;
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
