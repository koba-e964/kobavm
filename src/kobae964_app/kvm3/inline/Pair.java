package kobae964_app.kvm3.inline;


import java.util.HashMap;
import java.util.Map;

import kobae964_app.kvm3.*;
import kobae964_app.kvm3.ClassLoader;

public class Pair extends ClassCode{
	private long addr;
	static byte[] toBytes(int v)
	{
		byte[] out=new byte[4];
		for(int i=0;i<4;i++)
		{
			out[i]=(byte)v;
			v>>>=8;
		}
		return out;
	}
	static int toInt(byte[] ar)
	{
		int v=0;
		int i=0;
		for(byte b:ar)
		{
			v+=(b&0xff)<<(8*i);
			i++;
		}
		return v;
	}
	private Pair(){}
	public Pair(int fst,int snd)
	{
		int clzID=ClassLoader.getClassID("Pair");
		byte[] data=new byte[8];
		System.arraycopy(toBytes(fst), 0, data, 0, 4);
		System.arraycopy(toBytes(snd), 0, data, 4, 4);
		addr=Heap.create(clzID, data, 0);
	}
	/**
	 * Creates an Code Object with existing object.
	 * called by {@link ClassData#getField(KVMObject, String)}.
	 * @param addr
	 */
	@Deprecated
	public Pair(long addr)
	{
		this.addr=addr;
	}
	/**
	 * Creates an Code Object with existing object.
	 * called by {@link ClassData#getField(KVMObject, String)}.
	 * @param addr
	 */
	public static Pair createInstanceFromAddress(long addr)
	{
		Pair inst=new Pair();
		inst.addr=addr;
		return inst;
	}
	@Override
	public VarEntry getField(String name) {
		KVMObject obj=Heap.retrieve(addr);
		if(offsets.containsKey(name))
		{
			int offset=offsets.get(name);
			byte[] buf=new byte[4];
			System.arraycopy(obj.data,offset,buf,0,4);
			return new VarEntry(DataType.INT.ordinal(),toInt(buf));
		}
		return null;
	}

	@Override
	public void setField(String name, VarEntry value) {
		KVMObject obj=Heap.retrieve(addr);
		if(offsets.containsKey(name))
		{
			int offset=offsets.get(name);
			if(value.type!=DataType.INT.ordinal())
			{
				throw new ClassCastException();
			}
			byte[] buf=toBytes((int)value.value);
			System.arraycopy(buf,0,obj.data,offset,4);
			return;
		}
		throw new RuntimeException("Illegal member Pair."+name);
	}

	@Override
	public VarEntry call(String name, VarEntry... args) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getAddress() {
		return addr;
	}
	/**
	 * Retrieves a constant value from constant pool.
	 * 0:"fst", 1:"snd", 2:new Pair(0,0)
	 * @param id
	 * @return
	 */
	public static VarEntry getConstant(int id)
	{
		if(id<0 || id>=3)
		{
			throw new IllegalArgumentException("Illegal id:"+id+" at inline/Pair");
		}
		return new VarEntry(DataType.OBJECT.ordinal(),cpool[id]);
	}
	static Map<String,Integer> offsets=new HashMap<String, Integer>();
	/**
	 * constant pool.
	 */
	static long[] cpool;
	static
	{
		offsets.put("fst",0);
		offsets.put("snd",4);
		cpool=new long[3];
		cpool[0]=new KString("fst").getAddress();
		cpool[1]=new KString("snd").getAddress();
		cpool[2]=new Pair(0, 0).getAddress();
	}
}
