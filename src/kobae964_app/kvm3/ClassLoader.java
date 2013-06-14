package kobae964_app.kvm3;

import java.util.HashMap;
import java.util.Map;

import kobae964_app.kvm3.inline.Init;
import kobae964_app.kvm3.inline.KString;
import kobae964_app.kvm3.inline.Pair;

public class ClassLoader {
	static Map<String, Integer> table=new HashMap<String, Integer>();
	static Map<Integer,ClassData> dat=new HashMap<Integer,ClassData>();
	static int count=0;
	static{
		registerClass(KString.CLASS_NAME,KString.class);
		registerClass(Pair.CLASS_NAME,Pair.class);
		registerClass(Init.CLASS_NAME,Init.class);
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
	/**
	 * This method retrieves a constant value in the constant pool in specified class.
	 * This is equivalent to
	 * {@code {@link ClassLoader}.getClassData(classID).getConstant(id)}
	 * @param classID The class id which has the constant pool
	 * @param id #constant
	 * @return A constant value. The object indicated by it is const-obj.
	 */
	public static VarEntry getConstant(int classID,int id)
	{
		return getClassData(classID).getConstant(id);
	}
}
