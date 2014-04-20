package kobae964_app.kvm3.binclz;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * A linker that combine resources and make a big file.
 * @author koba-e964
 */
public class Linker {
	public Linker() {
		spool = new TreeMap<String, Integer>();
		mpool = new ArrayList<Object>(0x100);
		methods = new ArrayList<Object[]>(0x100);
		fields = new ArrayList<Object[]>(0x100);
	}
	/**
	 * Add val to the constant pool.
	 * If val is already added, this may return the previously returned id for val.
	 * @param val const value to add to this. val must be of type String, Long, Int, Double, and Object[].
	 * @return the id allocated for val.
	 */
	public int addConst(Object val) {
		int cpoolCount = mpool.size();
		if (val instanceof String) {
			if (spool.containsKey(val)) {
				return spool.get(val); // memoization of String.
			}
			spool.put((String)val, cpoolCount);
		}
		mpool.add(val);
		return cpoolCount;
	}
	/**
	 * 
	 * @param code the code contained by the object file
	 */
	public void setCode(byte[] code) {
		this.code = code.clone();
	}
	public int addMethod(String name, int numLocalVars, int offset, String sign) {
		int id = methods.size();
		methods.add(new Object[]{name, numLocalVars, offset, sign}); // UGLY code. The class that contains these data should be amde.
		return id;
	}
	public int addField(String name, int offset, String sign) {
		int id = fields.size();
		fields.add(new Object[]{name, offset, sign});
		return id;
	}
	public byte[] yieldCode(){
		byte[] header={'k','v','m',0x7f};
		assert header.length==4;
		if (code == null) {
			throw new RuntimeException("Code not initialized");
		}
		int headInfoLen=32;//bytes
		int codePos=header.length + headInfoLen;
		int cpoolPos = codePos + code.length;
		int cpoolLen=12 * mpool.size();
		int methodPos=codePos + code.length + cpoolLen;
		int methodLen=16 * methods.size();
		int fieldPos = methodPos + methodLen;
		int fieldLen = 12 * fields.size();
		byte[] cpool = createCPoolArray(cpoolPos);
		int[] method = createMethodArray();
		int[] field = createFieldArray();
		int[] headInfo=new int[]{
			codePos,//code-place
			code.length,//code-length
			cpoolPos,//cpool-place
			cpool.length,//cpool-length
			methodPos,//method-place
			4*method.length,
			fieldPos,//field-place
			4*field.length,//field-length
		};
		assert 4*headInfo.length == headInfoLen;
		byte[] ld = new byte[methodPos + 4 * method.length + 4 * field.length];
		System.arraycopy(header, 0, ld, 0, header.length);
		System.arraycopy(intsToBytes(headInfo), 0, ld, header.length, headInfoLen);
		System.arraycopy(code, 0, ld, codePos, code.length);
		System.arraycopy(cpool,  0, ld, cpoolPos,  cpoolLen);
		System.arraycopy(intsToBytes(method), 0, ld, methodPos, methodLen);
		System.arraycopy(intsToBytes(field),  0, ld, fieldPos,  fieldLen);
		return ld;
	}
	private int[] createFieldArray() {
		System.err.println("Linker.createFieldArray is not yet implemented.");
		return new int[0];
	}
	private int[] createMethodArray() {
		System.err.println("Linker.createMethodArray is not yet implemented.");
		return new int[0];
	}
	private byte[] createCPoolArray(int cpoolPos) {
		int size = mpool.size();
		int length = 3 * size; // length of the table in int
		int[] table = new int[length];
		int rsStart = cpoolPos + 4 * length;
		int rslen = 0;
		// 2-pass compiling
		for (int i = 0; i < size; ++i) {
			Object obj = mpool.get(i);
			if (obj instanceof String) {
				byte[] str = ((String) obj).getBytes();
				table[3*i] = Loader.STRING;
				table[3*i+1] = rsStart + rslen;
				table[3*i+2] = str.length;
				rslen += str.length;
				continue;
			}
			if (obj instanceof Long || obj instanceof Double) {
				int type;
				long value;
				if (obj instanceof Long) {
					type = Loader.INT;
					value = (Long) obj;
				} else {
					type = Loader.REAL;
					value = Double.doubleToRawLongBits((Double) obj);
				}
				table[3*i] = type;
				table[3*i+1] = (int) value;
				table[3*i+2] = (int) (value >>> 32L);
				rslen += 0;
				continue;
			}
			if (obj instanceof long[]) {
				throw new RuntimeException("long[] is not implemented");
			}
		}
		return intsToBytes(table); //TODO append resources
	}
	private byte[] intsToBytes(int[] array){
		byte[] result=new byte[array.length*4];
		for(int i=0;i<array.length;i++){
			int v=array[i];
			for(int j=0;j<4;j++){
				result[4*i+j]=(byte)(v>>>(8*j));
			}
		}
		return result;
	}
	private byte[] code;
	private List<Object[]> methods, fields;
	private TreeMap<String, Integer> spool; // memoized, should be quickly searched in.
	private List<Object> mpool; //misc.
}
