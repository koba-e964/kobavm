package kobae964_app.kvm3;
import static kobae964_app.kvm3.Flags.*;

/**
 * Internal expression of objects in VM.
 * Every instance of KVMObject should be created by Heap.
 *
 */
public class KVMObject {
	int typeInfo;
	public byte[] data;
	/**
	 * This constructor should be called only by {@link Heap#create(int, byte[], int)}.
	 * @param classID
	 * @param data
	 * @param flags
	 */
	KVMObject(int classID,byte[] data,int flags)
	{
		if((flags&IMMUTABLE)==IMMUTABLE)
		{
			throw new IllegalStateException("An object could not be both varobj and constobj.");
		}
		this.data=data;
		typeInfo=classID*4+(flags&IMMUTABLE)/16;//0-3
	}
	public int getClassID()
	{
		return typeInfo/4;
	}
	
	public boolean isVarObj()
	{
		return (typeInfo&(VAROBJ/16))!=0;
	}
	public boolean isConstObj()
	{
		return (typeInfo&(CONSTOBJ/16))!=0;
	}
}
