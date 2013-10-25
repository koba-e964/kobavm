package kobae964_app.kvm3;

import java.util.ArrayList;
import java.util.List;
import kobae964_app.kvm3.inline.KString;

import static kobae964_app.kvm3.DataType.*;

public class CallStack {
	public CallStack(){
		this(334);
	}
	public CallStack(int initialSize){
		data=new ArrayList<Integer>(3*initialSize);
	}
	private void push(int type,long val)
	{
		data.add(type);
		data.add((int)val);
		data.add((int)(val>>>32));
		ObjManager.refer(new VarEntry(type,val));//refer
	}
	public void push(VarEntry ve)
	{
		push(ve.type,ve.value);
	}
	public void pushReal(double value)
	{
		push(VarEntry.valueOf(value));
	}
	public void pushInt(long value)
	{
		push(INT.ordinal(),value);
	}
	public void pushBool(boolean value)
	{
		push(BOOL.ordinal(),value?1:0);
	}
	public void pushString(String value)
	{
		long addr=new KString(value).getAddress();
		pushObject(addr);
	}
	public void pushObject(long addr)
	{
		push(OBJECT.ordinal(),addr);
	}
	private void nullifyAt(int ind){
		VarEntry ve=getAt(ind);
		ObjManager.unrefer(ve);
		int size_3=data.size()-3;
		data.set(size_3-3*ind,-1);//fill with invalid value
		data.set(size_3-3*ind+1,-1);
		data.set(size_3-3*ind+2,-1);
	}
	public VarEntry pop()
	{
		if(data.size()==0){
			throw new IllegalStateException("Attempted to pop empty stack (in CallStack)");
		}
		VarEntry res=getAt(0);
		nullifyAt(0);
		data.subList(data.size()-3, data.size()).clear();
		return res;
	}
	public double popReal()
	{
		VarEntry result=pop();
		return result.toReal();
	}
	public long popInt()
	{
		VarEntry result=pop();
		result.checkDataType(INT);
		return result.value;
	}
	public boolean popBool()
	{
		VarEntry result=pop();
		result.checkDataType(BOOL);
		return result.value!=0;
	}
	public String popString()
	{
		VarEntry result=pop();
		result.checkDataType(OBJECT);
		long addr=result.value;
		return KString.getContent(addr);
	}
	private List<Integer> data;
	@Override
	public String toString(){
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
	public VarEntry getAt(int index)throws IndexOutOfBoundsException{
		checkIndex(index);
		int size_3=data.size()-3;
		int type=data.get(size_3-3*index);
		long value=data.get(size_3-3*index+1)&((1L<<32L)-1);//unsigned
		value|=(long)data.get(size_3-3*index+2)<<32L;
		return new VarEntry(type,value);
	}
	public void setAt(int index,VarEntry ve)throws IndexOutOfBoundsException{
		checkIndex(index);
		nullifyAt(index);
		ObjManager.refer(ve);
		int size_3=data.size()-3;
		data.set(size_3-3*index,ve.type);
		data.set(size_3-3*index+1,(int)ve.value);
		data.set(size_3-3*index+2,(int)(ve.value>>>32L));
	}
	private void checkIndex(int index)throws IndexOutOfBoundsException{
		if(index<0||3*index>=data.size()){
			throw new IndexOutOfBoundsException("Index is out of stack:"+index+" in "+"[0, "+size()+")");
		}
	}
	public int size(){
		return data.size()/3;
	}
}
