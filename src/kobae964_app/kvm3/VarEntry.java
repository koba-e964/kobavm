package kobae964_app.kvm3;

public class VarEntry implements Cloneable{
	public int type;
	public long value;
	public VarEntry(int type,long value)
	{
		this.type=type;
		this.value=value;
	}
	@Override
	public VarEntry clone()
	{
		try {
			return (VarEntry)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public String toString()
	{
		return "("+DataType.values()[type&Flags.TYPE_MASK].name()+", "+value+")";
	}
}
