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
	/**
	 * memory [0,free) is regarded to be allocated.
	 * This allocating system needs improvement.
	 */
	static int free=0;
	static Mem mem;
	static{
		registerClass(KString.CLASS_NAME,KString.class);
		registerClass(Pair.CLASS_NAME,Pair.class);
		registerClass(Init.CLASS_NAME,Init.class);
	}
	public static void setMem(Mem mem){
		ClassLoader.mem=mem;
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
	public static int registerClassWithBinary(String name,BinaryClassData dat){
		table.put(name, count);
		//loading
		int codesize=dat.code.length;
		mem.load(dat.code, free);
		//TODO:constant pool
		ClassData cd=new ClassData(count,name,dat,free,-1);
		ClassLoader.dat.put(count,cd);
		free+=codesize;
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
	public static int loadCode(byte[] code){
		int codesize=code.length;
		mem.load(code, free);
		int old=free;
		free+=codesize;
		return old;
	}
}
