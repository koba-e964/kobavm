package kobae964_app.kvm3;

public class VarEntry implements Cloneable{
	public int type;
	public long value;
	public VarEntry(int type,long value){
		this.type=type;
		this.value=value;
		if(type<0 || type>=DataType.values().length){
			//type is invalid
			throw new IllegalArgumentException("Illegal type:"+type);
		}
	}
	public VarEntry(DataType type,long value){
		this.type=type.ordinal();
		this.value=value;
	}
	@Override
	public VarEntry clone(){
		try {
			return (VarEntry)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
	@Override
	public String toString(){
		return "("+getType().toString()+", "+value+")";
	}
	/**
	 * Utility method which takes argument(double) and returns a {@link VarEntry} that holds the argument.
	 * @param value The content of returned {@link VarEntry}
	 * @return a VarEntry which holds {@code value}
	 */
	public static VarEntry valueOf(double value){
		return new VarEntry(DataType.REAL,Double.doubleToLongBits(value));
	}
	/**
	 * Utility method which converts {@code this} to a double value.
	 * @return converted value
	 */
	public double toReal()throws DataTypeMismatchException{
		this.checkDataType(DataType.REAL);
		return Double.longBitsToDouble(this.value);
	}
	/**
	 * Checks if type of this is {@code type}.
	 * If so, this method does nothing.
	 * Otherwise, this will throw an {@link RuntimeException}.
	 * @param type Type
	 */
	public void checkDataType(DataType type)throws DataTypeMismatchException{
		if(this.type!=type.ordinal()){
			throw new DataTypeMismatchException(type,this);
		}
	}
	public static class DataTypeMismatchException extends RuntimeException{
		private static final long serialVersionUID = 3498296753589214436L;
		public DataTypeMismatchException(String s){
			super(s);
		}
		public DataTypeMismatchException(DataType expected,VarEntry actual){
			this(expected+" was required, but "+actual.getType()+"was returned");
		}
	}
	public DataType getType(){
		return DataType.values()[type];
	}
}
