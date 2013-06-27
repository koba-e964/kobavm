package kobae964_app.kvm3;

import java.util.HashMap;

import java.util.Map;

import kobae964_app.kvm3.inline.Init;
import kobae964_app.kvm3.inline.KString;
import kobae964_app.kvm3.inline.Pair;
import static kobae964_app.kvm3.DataType.*;

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
		ClassData cd=new ClassData(count, name,null);
		dat.put(count,cd);
		return count++;
	}
	static public int registerClass(String name,Class<? extends ClassCode> clz)
	{
		table.put(name,count);
		ClassData cd=new ClassData(count, name,clz);
		dat.put(count,cd);
		return count++;
	}
	public static int registerClassWithBinary(String name,BinaryClassData dat){
		table.put(name, count);
		//loading
		int codesize=dat.code.length;
		mem.load(dat.code, free);
		//constant pool
		VarEntry[] addrs=new VarEntry[dat.constPool.length];
		for(int i=0,s=dat.constPool.length;i<s;i++){
			addrs[i]=registerConstant(dat.constPool[i]);
		}

		ClassData cd=new ClassData(count,name,dat,free,-1, addrs);
		ClassLoader.dat.put(count,cd);
		free+=codesize;
		return count++;
	}
	/**
	 * This method is used in {@link ClassLoader#registerClassWithBinary(String, BinaryClassData)}.
	 * @param obj an object to register with {@link Heap}
	 * @return the address returned by {@link Heap#create(int, byte[], int)}.
	 */
	private static VarEntry registerConstant(Object obj){
		System.out.println("Adding "+obj+"::"+obj.getClass());
		if(obj instanceof String){
			long addr=new KString((String)obj).getAddress();
			return new VarEntry(OBJECT, addr);
		}
		if(obj instanceof Number){
			Number num=(Number)obj;
			if(num instanceof Double||num instanceof Float){//real
				double value=num.doubleValue();
				return new VarEntry(REAL,Double.doubleToLongBits(value));
			}
			//integer
			return new VarEntry(INT,num.longValue());
		}
		throw new RuntimeException("Not implemented");
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
