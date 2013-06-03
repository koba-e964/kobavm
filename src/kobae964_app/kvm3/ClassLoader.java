package kobae964_app.kvm3;

import java.util.HashMap;
import java.util.Map;

import kobae964_app.kvm3.inline.KString;
import kobae964_app.kvm3.inline.Pair;

public class ClassLoader {
	static Map<String, Integer> table=new HashMap<String, Integer>();
	static Map<Integer,ClassData> dat=new HashMap<Integer,ClassData>();
	static int count=0;
	static
	{
		registerClass("String",KString.class);
		registerClass("Pair",Pair.class);
	}
	static public int registerClass(String name)
	{
		table.put(name,count);
		ClassData cd=new ClassData(count, false,name,null);
		dat.put(count,cd);
		return count++;
	}
	static public int registerClass(String name,Class<? extends ClassCode> clz)
	{
		table.put(name,count);
		ClassData cd=new ClassData(count, false,name,clz);
		dat.put(count,cd);
		return count++;
	}
	static public int getClassID(String name)
	{
		return table.get(name);
	}
	static public ClassData getClassData(int id)
	{
		return dat.get(id);
	}
	static public ClassData getClassData(String name)
	{
		return getClassData(table.get(name));
	}
	/**
	 * 
	 * @param addr address of object
	 * @param name the name of field
	 * @return
	 */
	static public VarEntry getField(long addr,String name)
	{
		KVMObject obj=Heap.retrieve(addr);
		int clzID=obj.getClassID();
		return getClassData(clzID).getField(obj, name);
	}
	/**
	 * 
	 * @param addr address of object
	 * @param name the name of field
	 * @return
	 */
	static public void setField(long addr,String name,VarEntry val)
	{
		KVMObject obj=Heap.retrieve(addr);
		int clzID=obj.getClassID();
		getClassData(clzID).setField(obj, name,val);
	}
}
