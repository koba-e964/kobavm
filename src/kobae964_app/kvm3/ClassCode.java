package kobae964_app.kvm3;

/**
 * The superclass of all classes that implement procedures executed in KVM.
 * Every subclass of {@link ClassCode} should have static method named {@code createInstanceFromAddress(long)} 
 * so that an instance can be create from the address of an object in {@link Heap}.
 * It also should implement class-specific constructors that initialize data from arguments
 * and register the data into {@link Heap} to get the address of it.
 * @author koba-e964
 *
 */
public abstract class ClassCode {
	/**
	 * Gets the address of this object in Heap.
	 * @return address(long)
	 */
	public abstract long getAddress();
	public abstract VarEntry getField(String name);
	public abstract void setField(String name,VarEntry value);
	public abstract VarEntry call(String name,VarEntry... args);
	/**
	 * By default this method always returns null reference(VarEntry that refers null, NOT {@code (VarEntry)null})
	 * Subclasses should override this method.
	 * @param id the ID of object in the constant pool
	 * @return
	 */
	public static VarEntry getConstant(int id)
	{
		return new VarEntry(DataType.OBJECT.ordinal(),Heap.NULL_ADDR);//NULL reference
	}
}
