package kobae964_app.kvm3;

/**
 * String used as internal expression
 *
 */
public class KString extends ClassCode{
	KVMObject var;
	String cont;
	public KString(KVMObject obj)
	{
		var=obj;
		cont=new String(obj.data);
	}
	public KString(String str,Heap heap)
	{
		heap.create(var=new KVMObject(0,str.getBytes(),Flags.CONSTOBJ));
		cont=str;
	}
	@Override
	public ClassCode create(KVMObject obj) {
		return new KString(obj);
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
		if(args.length==0&&name.equals("length"))
		{
			return new VarEntry(DataType.INT.ordinal(),cont.length());
		}
		if(args.length==1&&name.equals("charAt"))
		{
			return new VarEntry(DataType.INT.ordinal(),cont.charAt((int)args[0].value));
		}
		throw new UnsupportedOperationException();
	}
	public String getContent()
	{
		return cont;
	}
}
