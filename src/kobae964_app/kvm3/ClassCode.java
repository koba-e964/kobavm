package kobae964_app.kvm3;

public abstract class ClassCode {
	/**
	 * Gets the address of this object in Heap.
	 * @return address(long)
	 */
	public abstract long getAddress();
	public abstract VarEntry getField(String name);
	public abstract void setField(String name,VarEntry value);
	public abstract VarEntry call(String name,VarEntry... args);
}
