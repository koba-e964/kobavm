package kobae964_app.kvm3.inline;


import kobae964_app.kvm3.*;
import kobae964_app.kvm3.ClassLoader;

public class Pair extends ClassCode{
	long addr;
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
	public Pair(int fst,int snd)
	{
		int clzID=ClassLoader.getInstance().getClassID("Pair");
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
	public Pair(long addr)
	{
		this.addr=addr;
	}
	@Override
	public VarEntry getField(String name) {
		KVMObject obj=Heap.retrieve(addr);
		if(name.equals("fst"))
		{
			byte[] buf=new byte[4];
			System.arraycopy(obj.data,0,buf,0,4);
			return new VarEntry(DataType.INT.ordinal(),toInt(buf));
		}
		if(name.equals("snd"))
		{
			byte[] buf=new byte[4];
			System.arraycopy(obj.data,4,buf,0,4);
			return new VarEntry(DataType.INT.ordinal(),toInt(buf));
		}
		return null;
	}

	@Override
	public void setField(String name, VarEntry value) {
		KVMObject obj=Heap.retrieve(addr);
		if(name.equals("fst"))
		{
			if(value.type!=DataType.INT.ordinal())
			{
				throw new ClassCastException();
			}
			byte[] buf=toBytes((int)value.value);
			System.arraycopy(buf,0,obj.data,0,4);
			return;
		}
		if(name.equals("snd"))
		{
			if(value.type!=DataType.INT.ordinal())
			{
				throw new ClassCastException();
			}
			byte[] buf=toBytes((int)value.value);
			System.arraycopy(buf,0,obj.data,4,4);
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
		// TODO Auto-generated method stub
		return addr;
	}

}
