package kobae964_app.kvm3;

import static kobae964_app.kvm3.DataType.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import kobae964_app.kvm3.inline.KString;

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
	Map<String, Integer> methodTable,fieldTable;//name->index
	VarEntry[] cpool;//constants
	/**
	 * This constructor should be called only from ClassLoader.
	 * @param id classID
	 * @param name the name of class.
	 */
	ClassData(int id,String name,Class<? extends ClassCode> codeClz){
		idAttr=id*4;
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
		methodTable=new HashMap<String, Integer>();
		fieldTable=new HashMap<String, Integer>();
		vmInit();
	}
	private void vmInit(){
		//constant pool
		VarEntry[] addrs=new VarEntry[bdat.constPool.length];
		for(int i=0,s=bdat.constPool.length;i<s;i++){
			addrs[i]=registerConstant(bdat.constPool[i]);
		}
		cpool=addrs;
		//fields
		for(int i=0;i<bdat.fieldNames.length;i++){
			String mname=bdat.fieldNames[i];
			fieldTable.put(mname, i);
		}
		//methods
		for(int i=0;i<bdat.methodNames.length;i++){
			String mname=bdat.methodNames[i]+"."+bdat.methodSigns[i];
			methodTable.put(mname, i);
		}
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
		throw new UnsupportedOperationException("Unsupported Type:"+obj.getClass().getName());
	}
	public int classID(){
		return idAttr/4;
	}
	public String getName(){
		return name;
	}
	private long bytesToInt(byte[] array,int start,int end){
		long v=0;
		int c=0;
		for(int i=start;i<end;i++){
			byte b=array[i];
			v|=(b&0xff)<<(8*c);
			c++;
		}
		return v;
	}
	/**
	 * Retrieves a field(member variable) from an object.
	 * @param obj An object to get field from.
	 * @param name The name of variable
	 * @return a {@link VarEntry} holding the value.
	 */
	public VarEntry getField(KVMObject obj,String name){
		if(codeClz==null){
			int ind=fieldTable.get(name);
			int offset=bdat.fieldOffsets[ind];
			String sign=bdat.fieldSigns[ind];
			DataType type;
			long value;
			if(sign.equals("I")){//integer(64-bit)
				type=INT;
				value=bytesToInt(obj.data, offset, offset+8);
			}
			else if(sign.equals("4")){//32-bit integer
				type=INT;
				value=bytesToInt(obj.data, offset, offset+4);
			}
			else if(sign.equals("B")){//bool
				type=BOOL;
				value=bytesToInt(obj.data, offset, offset+4);
				value=value!=0?1:0;
			}
			else if(sign.equals("R")){//real
				type=REAL;
				value=bytesToInt(obj.data, offset, offset+8);
			}
			else{//object
				type=OBJECT;
				value=bytesToInt(obj.data, offset, offset+8);
			}
			return new VarEntry(type,value);
		}
		ClassCode inst;
		inst=getClassCodeInstance(obj);
		return inst.getField(name);
	}
	private ClassCode getClassCodeInstance(KVMObject obj) {
		long addr=Heap.toAddress(obj);
		return getClassCodeInstance(addr);
	}
	private ClassCode getClassCodeInstance(long addr) {
		if(codeClz==null){
			throw new RuntimeException("No ClassCode allocated");
		}
		try {
			return (ClassCode) codeClz.getMethod("createInstanceFromAddress", long.class).invoke(null, addr);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	public void setField(KVMObject obj,String name,VarEntry v){
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
		return isVMClass()&&methodTable.containsKey(name);
	}
	public int getVMCodeAddress(String name){
		return codePlace+methodTable.get(name);
	}
	public VarEntry getConstant(int id){
		VarEntry res;
		if(isVMClass()){
			if(0<=id&&id<cpool.length){
				return cpool[id];
			}
			throw new RuntimeException("No such constants:className="+name+", id="+id);
		}
		try {
			//TypeName.getConstant(id);
			res=(VarEntry) codeClz.getMethod("getConstant",int.class).invoke(null, id);
		} catch (Exception ex){
			throw new RuntimeException(ex);
		}
		return res;
	}
}
