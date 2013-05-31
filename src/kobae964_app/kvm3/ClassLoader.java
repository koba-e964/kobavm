package kobae964_app.kvm3;

import java.util.HashMap;
import java.util.Map;

public class ClassLoader {
	Map<String, Integer> table=new HashMap<String, Integer>();
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
		return count++;
	}
	public int getClassID(String name,boolean constobj)
	{
		return this.getRawClassID(name, constobj)*4+(constobj?Flags.CONSTOBJ/16:Flags.VAROBJ/16);
	}
	public int getRawClassID(String name,boolean constobj)
	{
		if(!table.containsKey(name))
		{
			throw new RuntimeException();
		}
		return table.get(name);
	}
}
