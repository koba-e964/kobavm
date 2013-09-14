package kobae964_app.kvm3.inline;

import kobae964_app.kvm3.ClassCode;
import kobae964_app.kvm3.ClassData;
import kobae964_app.kvm3.ClassLoader;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Flags;
import kobae964_app.kvm3.Heap;
import kobae964_app.kvm3.KVMObject;
import kobae964_app.kvm3.VarEntry;

/**
 * String used as internal expression
 *
 */
public class KString extends ClassCode{
	KVMObject var;
	String cont;
	long addr;
	private KString(){}
	public KString(KVMObject obj)
	{
		var=obj;
		cont=new String(obj.data);
	}
	public KString(String str)
	{
		int clzId=ClassLoader.getClassID(CLASS_NAME);
		addr=Heap.create(clzId,str.getBytes(),Flags.CONSTOBJ);
		var=Heap.retrieve(addr);
		cont=str;
	}
	/**
	 * called by {@link ClassData#getField(KVMObject, String)}.
	 * @param addr
	 */
	@Deprecated
	public KString(long addr)
	{
		this.addr=addr;
		var=Heap.retrieve(addr);
		cont=new String(var.data);
	}
	/**
	 * called by {@link ClassData#getField(KVMObject, String)}.
	 * @param addr address of the object
	 * @exception {@link IllegalArgumentException}
	 */
	public static KString createInstanceFromAddress(long addr) throws IllegalArgumentException
	{
		KString inst=new KString();
		inst.addr=addr;
		inst.var=Heap.retrieve(addr);
		if(inst.var.getClassID()!=ClassLoader.getClassID(CLASS_NAME))
		{
			throw new IllegalArgumentException("Invalid type (addr="+addr+")");
		}
		inst.cont=new String(inst.var.data);
		return inst;
	}
	public long getAddress()
	{
		return addr;
	}
	@Override
	public VarEntry getField(String name) {
		throw new RuntimeException();
	}

	@Override
	public void setField(String name, VarEntry value) {
		throw new RuntimeException();
	}

	@Override
	public VarEntry call(String name, VarEntry... args) {
		if(name.equals("length")){
			if(args.length!=0){
				throw new IllegalArgumentException(CLASS_NAME+".length() takes 0 argument"); 
			}
			return new VarEntry(DataType.INT,cont.length());
		}
		if(name.equals("charAt")){
			if(args.length!=1){
				throw new IllegalArgumentException(CLASS_NAME+".charAt(int) takes 1 argument"); 
			}
			args[0].checkDataType(DataType.INT);
			return new VarEntry(DataType.INT,cont.charAt((int)args[0].value));
		}
		throw new UnsupportedOperationException();
	}
	public String getContent()
	{
		return cont;
	}
	/**
	 * If the object specified by addr is a KString, this method is equivalent to
	 * {@code new KString(Heap.retrieve(addr)).getContent()}
	 * and
	 * {@code KString.createInstanceFromAddress(addr).getContent()}.
	 * Otherwise, this willÅ@throw an IllegalArgumentException.
	 * @param addr KString object
	 * @return {@link IllegalArgumentException}
	 */
	public static String getContent(long addr)throws IllegalArgumentException
	{
		return KString.createInstanceFromAddress(addr).getContent();
	}
	/**
	 * The internal name of this class.
	 */
	public static final String CLASS_NAME="String";
}
