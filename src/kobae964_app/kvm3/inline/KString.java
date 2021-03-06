package kobae964_app.kvm3.inline;

import kobae964_app.kvm3.ClassCode;
import kobae964_app.kvm3.ClassData;
import kobae964_app.kvm3.ClassLoader;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Flags;
import kobae964_app.kvm3.Heap;
import kobae964_app.kvm3.KVMObject;
import kobae964_app.kvm3.VarEntry;
import kobae964_app.kvm3.VarEntry.DataTypeMismatchException;

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
		cont=new String(obj.getContent());
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
		cont=new String(var.getContent());
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
		if(addr==Heap.NULL_ADDR){//static call of method
			inst.var=null;
			inst.cont=null;
			return inst;
		}
		inst.var=Heap.retrieve(addr);
		if(inst.var.getClassID()!=ClassLoader.getClassID(CLASS_NAME))
		{
			throw new IllegalArgumentException("Invalid type (addr="+addr+")");
		}
		inst.cont=new String(inst.var.getContent());
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
	/**
	 * methods:
	 * int length();
	 * char charAt(int);
	 * static String create(char[]);
	 * String substring(int,int);
	 * String concat(String);
	 * 
	 * @param name method's name
	 * @param args arguments
	 */
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
		if(name.equals("create")){
			if(args.length!=1){
				throw new IllegalArgumentException(CLASS_NAME+".create(char[]) takes 1 argument");
			}
			args[0].checkDataType(DataType.OBJECT);
			KArray inst=KArray.createInstanceFromAddress(args[0].value);
			int length=(int)inst.getField("length").value;
			char[] back=new char[length];
			for(int i=0;i<length;i++){
				VarEntry ve=inst.call("get", VarEntry.valueOf(i));
				ve.checkDataType(DataType.INT);
				back[i]=(char)ve.value;
			}
			String out=new String(back);
			return VarEntry.valueOf(out);
		}
		if(name.equals("substring")){
			if(args.length!=2){
				throw new IllegalArgumentException(CLASS_NAME+".substring(int,int) takes 2 arguments");
			}
			args[0].checkDataType(DataType.INT);
			args[1].checkDataType(DataType.INT);
			int a=(int)args[0].value;
			int b=(int)args[1].value;
			return VarEntry.valueOf(cont.substring(a,a+b));
		}
		if(name.equals("concat")){
			if(args.length!=1){
				throw new IllegalArgumentException(CLASS_NAME+".concat(String) takes 1 argument");
			}
			args[0].checkDataType(DataType.OBJECT);
			String another=KString.getContent(args[0].value);
			return VarEntry.valueOf(cont+another);
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
	 * Otherwise, this will�@throw an IllegalArgumentException.
	 * @param addr KString object
	 * @return {@link IllegalArgumentException}
	 */
	public static String getContent(long addr)throws IllegalArgumentException
	{
		return KString.createInstanceFromAddress(addr).getContent();
	}
	/**
	 * If the object specified by entry is a KString, this method is equivalent to
	 * {@code new KString(Heap.retrieve(entry.value)).getContent()}
	 * and
	 * {@code KString.createInstanceFromAddress(entry.value).getContent()}.
	 * Otherwise, this will�@throw an IllegalArgumentException.
	 * @param entry a {@link VarEntry} describing KString object
	 * @return {@link IllegalArgumentException}
	 */
	public static String getContent(VarEntry entry)throws IllegalArgumentException, DataTypeMismatchException{
		entry.checkDataType(DataType.OBJECT);
		long addr=entry.value;
		return KString.createInstanceFromAddress(addr).getContent();
	}
	/**
	 * The internal name of this class.
	 */
	public static final String CLASS_NAME="String";
}
