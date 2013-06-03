package kobae964_app.kvm3;

import java.lang.reflect.InvocationTargetException;

/**
 * ClassData
 * created only by ClassLoader
 */
public class ClassData {
	int idAttr;
	String name;
	Class<? extends ClassCode> codeClz;
	/**
	 * This constructor should be called only from ClassLoader.
	 * @param id
	 * @param isConstObj
	 * @param name
	 */
	ClassData(int id,boolean isConstObj,String name,Class<? extends ClassCode> codeClz)
	{
		idAttr=id*4+(isConstObj?Flags.CONSTOBJ/16:Flags.VAROBJ/16);
		this.name=name;
		this.codeClz=codeClz;
	}
	public int classID()
	{
		return idAttr/4;
	}
	public boolean isConstObj()
	{
		return idAttr%4==Flags.CONSTOBJ/16;
	}
	public String getName()
	{
		return name;
	}
	public VarEntry getField(KVMObject obj,String name)
	{
		if(codeClz==null)
		{
			throw new RuntimeException("No ClassCode allocated");
		}
		long addr=Heap.toAddress(obj);
		ClassCode inst;
		try {
			//new TypeName(addr);
			inst=codeClz.getConstructor(long.class).newInstance(addr);
		}catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
		return inst.getField(name);
	}
	public void setField(KVMObject obj,String name,VarEntry v)
	{
		System.out.println("SetField "+name+"<-"+v+" in "+this.name);		
	}
}
