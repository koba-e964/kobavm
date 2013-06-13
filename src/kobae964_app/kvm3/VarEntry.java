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
	/**
	 * Checks if type of this is {@code type}.
	 * If so, this method does nothing.
	 * Otherwise, this will throw an {@link RuntimeException}.
	 * @param type Type
	 */
	public void checkDataType(DataType type){
		if((this.type&Flags.TYPE_MASK)!=type.ordinal()){
			throw new RuntimeException(this+" is not "+type);
		}
	}
}
