package kobae964_app.kvm3;

public abstract class ClassCode {
	public abstract ClassCode create(KVMObject obj);
	public abstract VarEntry getField(String name);
	public abstract void setField(String name,VarEntry value);
	public abstract VarEntry call(String name,VarEntry... args);
}
