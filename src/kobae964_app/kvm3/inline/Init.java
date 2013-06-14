package kobae964_app.kvm3.inline;

import kobae964_app.kvm3.ClassCode;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Heap;
import kobae964_app.kvm3.VarEntry;

/**
 * The class that is initially loaded by CPU.
 * 
 * @author koba-e964
 *
 */
public class Init extends ClassCode{
	
	/**
	 * There are no members in {@link Init}.
	 */
	@Override
	public long getAddress() {
		return Heap.NULL_ADDR;
	}

	@Override
	public VarEntry getField(String name) {
		return null;
	}

	@Override
	public void setField(String name, VarEntry value) {
	}

	@Override
	public VarEntry call(String name, VarEntry... args) {
		return null;
	}
	public static VarEntry getConstant(int id){
		return new VarEntry(DataType.INT.ordinal(),12345);
	}
	public static final String CLASS_NAME="Init";
}
