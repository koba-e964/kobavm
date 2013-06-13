package kobae964_app.kvm3;

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
		ClassCode inst;
		inst=getClassCodeInstance(obj);
		return inst.getField(name);
	}
	private ClassCode getClassCodeInstance(KVMObject obj) {
		if(codeClz==null){
			throw new RuntimeException("No ClassCode allocated");
		}
		long addr=Heap.toAddress(obj);
		try {
			return (ClassCode) codeClz.getMethod("createInstanceFromAddress", long.class).invoke(null, addr);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	public void setField(KVMObject obj,String name,VarEntry v)
	{
		ClassCode inst;
		inst=getClassCodeInstance(obj);
		inst.setField(name,v);
	}
	public VarEntry getConstant(int id)
	{
		VarEntry res;
		try {
			//TypeName.getConstant(id);
			res=(VarEntry) codeClz.getMethod("getConstant",int.class).invoke(null, id);
		} catch (Exception ex){
			throw new RuntimeException(ex);
		}
		return res;
	}
}
