package kobae964_app.kvm3;

import kobae964_app.kvm3.inline.KString;

public final class VarEntry implements Cloneable{
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
	 * Utility method which takes argument(Object) and returns a {@link VarEntry} that holds the argument.
	 * {@code value} must be an instance of {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link Character}, or {@link String}.
	 * @param value The content of returned {@link VarEntry}
	 * @return a VarEntry which holds {@code value}
	 * @exception IllegalArgumentException if {@code value} is not convertible to VarEntry
	 */
	public static VarEntry valueOf(Object value)throws IllegalArgumentException{
		if(value instanceof Number){
			if(value instanceof Byte
			|| value instanceof Short
			|| value instanceof Integer
			|| value instanceof Long){//a kind of integer
				return new VarEntry(DataType.INT,((Number)value).longValue());
			}
			if(value instanceof Float
			|| value instanceof Double){//a kind of real
				return valueOf(((Number)value).doubleValue());
			}
			//value.getClass() is not a wrapper of primitive type
			//throw new IllegalArgumentException(...)
		}if(value instanceof Boolean){
			return new VarEntry(DataType.BOOL,(Boolean)value?1:0);
		}if(value instanceof Character){
			return new VarEntry(DataType.INT,(Character)value);
		}if(value instanceof String){
			return new VarEntry(DataType.OBJECT,new KString((String)value).getAddress());
		}
		throw new IllegalArgumentException("not convertible to VarEntry:"+value+":"+value.getClass().getName());
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
	@Override
	public boolean equals(Object another){
		if(!(another instanceof VarEntry))return false;
		return equals((VarEntry)another);
	}
	public boolean equals(VarEntry another){
		return type==another.type && value==another.value;
	}
	public static class DataTypeMismatchException extends RuntimeException{
		private static final long serialVersionUID = 3498296753589214436L;
		public DataTypeMismatchException(String s){
			super(s);
		}
		public DataTypeMismatchException(DataType expected,VarEntry actual){
			this(expected+" was required, but "+actual.getType()+" was returned");
		}
	}
	public DataType getType(){
		return DataType.values()[type];
	}
}
