package kobae964_app.kvm3.binclz;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

import kobae964_app.kvm3.BinaryClassData;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.VarEntry;

public class Loader {
	private static final String FIELD_PLACE = "field_place";
	private static final String METHOD_PLACE = "method_place";
	private static final String CONSTPOOL_PLACE = "constpool_place";
	private static final String CODE_PLACE = "code_place";
	public Loader(byte[] source){
		init(source.clone());
	}
	public Loader(File file) throws IOException{
		RandomAccessFile raf = null;
		try{
			raf=new RandomAccessFile(file, "r");
			long sizeL=raf.length();
			if(sizeL>=0x10000000L){
				throw new RuntimeException("Too large:"+file);
			}
			byte[] array=new byte[(int)sizeL];
			raf.seek(0L);
			raf.read(array);
			init(array);
		}catch(IOException ex){
			throw ex;
		}finally{
			if(raf!=null)
				raf.close();
		}
	}
	void init(byte[] source){
		this.source=source;
		int[] code_place=getData(CODE_PLACE);
		int[] constpool_place=getData(CONSTPOOL_PLACE);
		int[] method_place=getData(METHOD_PLACE);
		int[] field_place=getData(FIELD_PLACE);
		bdat=new BinaryClassData();
		bdat.code=getCode(code_place[0],code_place[1]);
		bdat.constPool=getConstPool(constpool_place[0], constpool_place[1]);
	}
	byte[] getCode(int start,int length){
		byte[] out=new byte[length];
		System.arraycopy(source, start, out, 0, length);
		return out;
	}
	Object[] getConstPool(int start,int length){
		assert length%12==0;
		Object[] out=new Object[length/12];
		for(int i=0;i<length;i+=12){
			int type=bytesToInt(source, start+i,4);
			long data=bytesToLong(source, start+i+4, 8);
			Object inserted;
			switch(type){
			case INT:
				inserted=(Long)data;
				break;
			case STRING:
			{
				int strpos=(int)data;
				int strlen=(int)(data>>>32L);
				byte[] b=new byte[strlen];
				System.arraycopy(source, strpos, b, 0, strlen);
				inserted=new String(b);
				break;
			}
			case REAL:
				inserted=(Double)Double.longBitsToDouble(data);
				break;
			case ARRAY://integer array
			{
				int arypos=(int)data;
				int arylen=(int)(data>>>32L);
				assert arylen%8==0;
				long[] ary=new long[arylen/8];
				for(int j=0;j<arylen;j+=8){
					long value=bytesToLong(source, arypos+j, 8);
					ary[j/8]=value;
				}
				inserted=ary;
				break;
			}
			default:
				throw new VarEntry.DataTypeMismatchException("Invalid Type");
			}
			out[i/12]=inserted;
		}
		return out;
	}
	byte[] source;
	int[] getData(String name){
		for(Form f:format){
			if(f.name.equals(name)){
				assert f.length%4==0;
				int offset=f.offset;
				int[] dat=new int[f.length/4];
				for(int i=0;i<f.length;i+=4){
					dat[i/4]=bytesToInt(source, offset+i, 4);
				}
				return dat;
			}
		}
		throw new RuntimeException(new NoSuchFieldException("No field named "+name));
	}
	static int bytesToInt(byte[] ar,int start,int length){
		int v=0;
		for(int i=0;i<length&&i<4;i++){
			v|=(ar[start+i]&0xff)<<(8*i);
		}
		return v;
	}
	static long bytesToLong(byte[] ar,int start,int length){
		long v=0;
		for(int i=0;i<length&&i<8;i++){
			v|=(ar[start+i]&0xffL)<<(8*i);
		}
		return v;
	}
	static final class Form{
		String name;
		int offset;
		int length;//bytes
		Form(String name,int offset,int length){
			this.name=name;
			this.offset=offset;
			this.length=length;
		}
		@Override
		public boolean equals(Object another){
			if(!(another instanceof Form)){
				return false;
			}
			Form ano=(Form)another;
			return name.equals(ano.name)&&offset==ano.offset&&length==ano.length;
		}
		@Override
		public int hashCode(){
			return name.hashCode()^offset^length;
		}
	}
	BinaryClassData bdat;
	private static Set<Form> format;
	static{
		format=new HashSet<Loader.Form>();
		format.add(new Form(CODE_PLACE,4,8));
		format.add(new Form(CONSTPOOL_PLACE,12,8));
		format.add(new Form(METHOD_PLACE,20,8));
		format.add(new Form(FIELD_PLACE,28,8));
	}
	public static final int 
		INT=0,
		STRING=1,
		REAL=2,
		ARRAY=3;
}
