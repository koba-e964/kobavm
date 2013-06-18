package kobae964_app.kvm3;

/**
 * ClassData
 * created only by ClassLoader
 */
public class ClassData {
	int idAttr;
	String name;
	/**
	 * if codeClz is null, this class has native code.
	 */
	Class<? extends ClassCode> codeClz;
	/*
	 * for native code
	 */
	BinaryClassData bdat;
	int codePlace,dataPlace;
	/**
	 * This constructor should be called only from ClassLoader.
	 * @param id classID
	 * @param isConstObj not used.
	 * @param name the name of class.
	 */
	ClassData(int id,boolean isConstObj,String name,Class<? extends ClassCode> codeClz)
	{
		idAttr=id*4+(isConstObj?Flags.CONSTOBJ/16:Flags.VAROBJ/16);
		this.name=name;
		this.codeClz=codeClz;
	}
	ClassData(int id,String name,BinaryClassData bdat,int codePlace,int dataPlace){
		idAttr=id*4;
		this.name=name;
		this.codeClz=null;
		this.bdat=bdat;
		this.codePlace=codePlace;
		this.dataPlace=dataPlace;
	}
	public int classID()
	{
		return idAttr/4;
	}
	@Deprecated
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
		return getClassCodeInstance(addr);
	}
	private ClassCode getClassCodeInstance(long addr) {
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
	/**
	 * 
	 * @param name The name of method.
	 * @param addr address of instance. If the method is statically called, addr should be {@link Heap#NULL_ADDR}. 
	 * @param args Arguments.
	 * @return returned value of the method. If the type of returned type of the method is void,
	 * this method returns null.
	 * 
	 */
	public VarEntry call(long addr,String name,VarEntry... args){
		if(hasVMCode(name)){
			throw new RuntimeException("VM Code should not be called in such a way.");
		}
		ClassCode inst;
		inst=getClassCodeInstance(addr);
		return inst.call(name, args);
	}
	public boolean isVMClass(){
		return codeClz==null;
	}
	public int getCodePlace(){
		return codePlace;
	}
	public int getDataPlace(){
		return dataPlace;
	}
	/**
	 * 
	 * @param name mangled name of method(such as "Test.I")
	 * @return 
	 */
	public boolean hasVMCode(String name){
		return isVMClass();
	}
	public int getVMCodeAddress(String name){
		return codePlace;//TODO This value needs to be fixed.
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
