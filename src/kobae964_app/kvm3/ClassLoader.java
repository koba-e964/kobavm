package kobae964_app.kvm3;

import java.util.HashMap;
import java.util.Map;

import kobae964_app.kvm3.inline.Pair;

public class ClassLoader {
	Map<String, Integer> table=new HashMap<String, Integer>();
	Map<Integer,ClassData> dat=new HashMap<Integer,ClassData>();
	int count=0;
	static ClassLoader inst=new ClassLoader();
	Map<Integer,Class<? extends ClassCode>> codes=new HashMap<Integer, Class<? extends ClassCode>>();
	public static ClassLoader getInstance()
	{
		return inst;
	}
	ClassLoader()
	{
		registerClass("String",KString.class);
		registerClass("Pair",Pair.class);
	}
	public int registerClass(String name)
	{
		table.put(name,count);
		ClassData cd=new ClassData(count, false,name,null);
		dat.put(count,cd);
		return count++;
	}
	public int registerClass(String name,Class<? extends ClassCode> clz)
	{
		table.put(name,count);
		ClassData cd=new ClassData(count, false,name,clz);
		dat.put(count,cd);
		codes.put(count, clz);
		return count++;
	}
	public int getClassID(String name)
	{
		return table.get(name);
	}
	public ClassData getClassData(int id)
	{
		return dat.get(id);
	}
	public ClassData getClassData(String name)
	{
		return getClassData(table.get(name));
	}
	/**
	 * 
	 * @param addr address of object
	 * @param name
	 * @return
	 */
	public VarEntry getField(long addr,String name)
	{
		KVMObject obj=Heap.retrieve(addr);
		int clzID=obj.getClassID();
		return getClassData(clzID).getField(obj, name);
	}
}
