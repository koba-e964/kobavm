package kobae964_app.kvm3.inline;

import kobae964_app.kvm3.ClassCode;
import kobae964_app.kvm3.ClassLoader;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Heap;
import kobae964_app.kvm3.KVMObject;
import kobae964_app.kvm3.VarEntry;
/**
 * An instance of KArray represents an array 
 * @author koba-e964
 *
 */
public class KArray extends ClassCode {
	long addr;
	private static long toInt(byte[] array,int start,int len){
		long v=0;
		for(int i=0;i<len&&i<8;i++){
			v|=(array[start+i]&0xffL)<<(8*i);
		}
		return v;
	}
	private static VarEntry getVarEntry(byte[] array,int start){
		int type=(int)toInt(array, start, 4);
		long value=toInt(array, start+4, 8);
		return new VarEntry(type, value);
	}
	private static void setInt(byte[] array,int start,int len,int value){
		for(int i=0;i<len&&i<8;i++){
			array[start+i]=(byte)(value>>>(8*i));
		}
	}
	private static void setVarEntry(byte[] array,int start,VarEntry ve){
		setInt(array,start,4,ve.type);
		setInt(array,start+4,4,(int)ve.value);
		setInt(array,start+8,4,(int)(ve.value>>>32));
	}
	private KArray(){
		//do nothing
	}
	public KArray(Object[] array) {
		commonInit(array.length);
		KVMObject obj=Heap.retrieve(addr);
		for(int i=0;i<array.length;i++){
			setVarEntry(obj.data,4+12*i,VarEntry.valueOf(array[i]));
		}
	}
	public KArray(int length){
		commonInit(length);
		KVMObject obj=Heap.retrieve(addr);
		for(int i=0;i<length;i++){
			setVarEntry(obj.data,4+12*i,new VarEntry(DataType.OBJECT,Heap.NULL_ADDR));//null
		}
	}
	void commonInit(int length){
		int thisClassID=ClassLoader.getClassID(CLASS_NAME);
		byte[] data=new byte[4+length*12];
		setInt(data,0,4,length);
		addr=Heap.create(thisClassID, data, 0);
	}

	@Override
	public long getAddress() {
		return addr;
	}
	public static KArray createInstanceFromAddress(long addr){
		KArray inst=new KArray();
		inst.addr=addr;
		if(addr==Heap.NULL_ADDR){
			return inst;
		}
		//addr!=NULL
		KVMObject var=Heap.retrieve(addr);
		if(var.getClassID()!=ClassLoader.getClassID(CLASS_NAME)){
			throw new IllegalArgumentException("Invalid type (addr="+addr+")");
		}
		return inst;
	}
	/**
	 * int length;(read-only)
	 */
	@Override
	public VarEntry getField(String name) {
		if(name.equals("length")){
			KVMObject obj=Heap.retrieve(addr);
			return VarEntry.valueOf(toInt(obj.data,0,4));
		}
		throw new UnsupportedOperationException("attempted to getField name="+name+", addr="+addr);
	}

	@Override
	public void setField(String name, VarEntry value) {
		throw new UnsupportedOperationException("attempted to setField name="+name+", value="+value+", addr="+addr);
	}
	/**
	 * methods:
	 * T get(int)
	 * void set(int,T)
	 */
	@Override
	public VarEntry call(String name, VarEntry... args) {
		if(name.equals("get")){
			if(args.length!=1){
				throw new IllegalArgumentException(CLASS_NAME+".get(int) takes 1 argument");
			}
			args[0].checkDataType(DataType.INT);
			int idx=(int)args[0].value;
			KVMObject obj=Heap.retrieve(addr);
			int len=(int)toInt(obj.data,0,4);
			if(idx<0 || idx>=len){
				throw new ArrayIndexOutOfBoundsException(idx);
			}
			return getVarEntry(obj.data,4+12*idx);
		}
		if(name.equals("set")){
			if(args.length!=2){
				throw new IllegalArgumentException(CLASS_NAME+".set(int) takes 2 argument");
			}
			args[0].checkDataType(DataType.INT);
			int idx=(int)args[0].value;
			KVMObject obj=Heap.retrieve(addr);
			int len=(int)toInt(obj.data,0,4);
			if(idx<0 || idx>=len){
				throw new ArrayIndexOutOfBoundsException(idx);
			}
			setVarEntry(obj.data, 4+12*idx, args[1]);
			return null;//void
		}
		StringBuilder sb=new StringBuilder("undefined method name="+name+", args=[");
		for(VarEntry ent:args){
			sb.append(ent);
			sb.append(", ");
		}
		throw new UnsupportedOperationException(sb.toString());
	}
	public static VarEntry getConstant(int id){
		throw new RuntimeException();
	}
	public static final String CLASS_NAME="KArray";

}
