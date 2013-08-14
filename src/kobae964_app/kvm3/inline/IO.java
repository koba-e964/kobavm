package kobae964_app.kvm3.inline;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kobae964_app.kvm3.ClassCode;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Heap;
import kobae964_app.kvm3.VarEntry;


/**
 * The class IO handles with I/O.
 * This class has only static methods.
 * @author koba-e964
 *
 */
public class IO extends ClassCode {
	/**
	 * Default constructor. 
	 */
	private IO(){}
	@Override
	public long getAddress() {
		throw new RuntimeException();
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
	 * int open(String name);
	 * int putchar(int fd,int ch);
	 * int getchar(int fd);
	 * int flush(int fd);
	 * int close(int fd);
	 */
	@Override
	public VarEntry call(String name, VarEntry... args) {
		if(name.equals("open")){
			if(args.length!=2){
				throw new IllegalArgumentException();
			}
			String fname;
			args[0].checkDataType(DataType.OBJECT);
			fname=KString.getContent(args[0].value);

			int attr;
			args[1].checkDataType(DataType.INT);
			attr=(int)args[1].value;
			int res;
			res = open(fname,attr);
			return new VarEntry(DataType.INT,res);
		}
		if(name.equals("putchar")){
			if(args.length!=2){
				throw new IllegalArgumentException();
			}
			int fd;
			args[0].checkDataType(DataType.INT);
			fd=(int)args[0].value;

			int ch;
			args[1].checkDataType(DataType.INT);
			ch=(int)args[1].value;
			int res=putchar(fd,ch);
			return new VarEntry(DataType.INT,res);
		}
		if(name.equals("getchar")){
			if(args.length!=1){
				throw new IllegalArgumentException();
			}
			int fd;
			args[0].checkDataType(DataType.INT);
			fd=(int)args[0].value;
			int res=getchar(fd);
			return new VarEntry(DataType.INT,res);
		}
		if(name.equals("flush")){
			if(args.length!=1){
				throw new IllegalArgumentException();
			}
			int fd;
			args[0].checkDataType(DataType.INT);
			fd=(int)args[0].value;
			int res=flush(fd);
			return new VarEntry(DataType.INT,res);
		}
		if(name.equals("close")){
			if(args.length!=1){
				throw new IllegalArgumentException();
			}
			int fd;
			args[0].checkDataType(DataType.INT);
			fd=(int)args[0].value;
			int res=close(fd);
			return new VarEntry(DataType.INT,res);
		}
		throw new RuntimeException("no such method:"+name);
	}
	public static IO createInstanceFromAddress(long addr){
		if(addr!=Heap.NULL_ADDR){
			throw new IllegalArgumentException("IO cannot be instantiated. addr has to be NULL.");
		}
		assert addr==Heap.NULL_ADDR;
		return new IO();
	}
	public static VarEntry getConstant(int id){
		throw new UnsupportedOperationException("IO.getConstant is not supported.");
	}
	/**
	 * [internal method] opens a file in reading/writing mode.
	 * @param name name
	 * @param attr if 1, writing mode; if 0, reading mode.
	 * @return file descriptor of opened file
	 */
	private synchronized static int open(String name,int attr){
		if(table.containsKey(name)){
			return table.get(name);
		}
		Str str;
		try{
			str=new Str(name,attr!=0);
		}catch(IOException ex){
			return -1;
		}
		table.put(name, count);
		map.put(count,str);
		count++;
		return count-1;
	}
	private static int putchar(int fd,int ch){
		Str str=map.get(fd);
		return str.putchar(ch);
	}
	private static int getchar(int fd){
		Str str=map.get(fd);
		return str.getchar();
	}
	private static int flush(int fd){
		Str str=map.get(fd);
		return str.flush();
	}
	private static int close(int fd){
		Str str=map.get(fd);
		int res=str.close();
		map.remove(fd);
		for(Entry<String,Integer> e:table.entrySet()){
			if(e.getValue().intValue()==fd){
				table.remove(e.getKey());
				break;
			}
		}
		return res;
	}
	private static Map<String,Integer> table=new HashMap<String, Integer>();
	private static Map<Integer, Str> map=new HashMap<Integer, IO.Str>();
	private static int count=0;
	public static final String CLASS_NAME="IO";
	private final static class Str{
		boolean write;
		OutputStream os;
		InputStream is;
		boolean isStd;
		Str(String name,boolean write) throws FileNotFoundException{
			if(name.equals("-")){
				isStd=true;
				return;
			}
			isStd=false;
			this.write=write;
			if(write){
				os=new FileOutputStream(name);
			}else{
				is=new FileInputStream(name);
			}
		}
		List<Byte> buf=new ArrayList<Byte>();
		int putchar(int ch){
			checkOpen();
			if(isStd){
				System.out.println((char)ch);
				return 0;
			}
			if(!write)throw new RuntimeException();
			buf.add((byte)ch);
			return 0;
		}
		int getchar(){
			checkOpen();
			if(isStd){
				try {
					return new InputStreamReader(System.in).read();
				} catch (IOException e) {
					return -1;
				}
			}
			if(write)throw new RuntimeException();
			try {
				return is.read();
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}
		int flush(){
			checkOpen();
			if(!write)throw new RuntimeException();
			if(isStd)return 0;
			try{
				for(byte a:buf){
					os.write(a&0xff);
				}
			}catch(IOException ex){
				return -1;
			}finally{
				buf.clear();
			}
			return 0;
		}
		/**
		 * closed instance will never exist.
		 * @return 0:success negative:failure
		 */
		int close(){
			checkOpen();
			if(write)
				flush();
			try{
				if(write)os.close();
				else is.close();
			}catch(IOException ex){
				return -1;
			}
			os=null;
			is=null;
			return 0;
		}
		void checkOpen()throws IllegalStateException{
			if(isStd){
				return;
			}
			if(write){
				if(os==null)throw new IllegalStateException();
			}else{
				if(is==null)throw new IllegalStateException();
			}
		}
	}
}
