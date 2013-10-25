package kobae964_app.kvm3;
/**
 * The interface class for {@link Heap}.
 */
public final class ObjManager {
	/**
	 * This class cannot be instantiated.
	 */
	private ObjManager() {
	}
	/**
	 * If objEntry is a VarEntry describing object, this method will increment refc.
	 * otherwise, this function does nothing.
	 * @param objEntry probable object(nullable)
	 */
	public static void refer(VarEntry objEntry){
		if(objEntry!=null && objEntry.getType()==DataType.OBJECT && objEntry.value!=NULL_ADDR)
			Heap.refer(objEntry.value);
	}
	/**
	 * If objEntry is a VarEntry describing object, this method will decrement refc.
	 * otherwise, this function does nothing.
	 * @param objEntry probable object(nullable)
	 */
	public static void unrefer(VarEntry objEntry){
		if(objEntry!=null && objEntry.getType()==DataType.OBJECT && objEntry.value!=NULL_ADDR)
			Heap.unrefer(objEntry.value);
	}
	public static void dumpAll(){
		Heap.dumpAll();
	}
	/**
	 * The address for null reference.
	 */
	public static final long NULL_ADDR=Heap.NULL_ADDR;
}
