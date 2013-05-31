package kobae964_app.kvm3;

import java.util.HashMap;
import java.util.Map;

public class ClassLoader {
	Map<String, Integer> table=new HashMap<String, Integer>();
	Map<Integer,ClassData> dat=new HashMap<Integer,ClassData>();
	int count=0;
	static ClassLoader inst=new ClassLoader();
	public static ClassLoader getInstance()
	{
		return inst;
	}
	ClassLoader()
	{
		registerClass("koba.lang.Object");
		registerClass("koba.lang.String");
	}
	public int registerClass(String name)
	{
		table.put(name,count);
		ClassData cd=new ClassData(count, false,name);
		dat.put(count,cd);
		return count++;
	}
	public ClassData getClassData(int id)
	{
		return dat.get(id);
	}
	public ClassData getClassData(String name)
	{
		return getClassData(table.get(name));
	}
}
