package kobae964_app.kvm3;

import java.util.HashMap;
import java.util.Map;

public class Heap {
	static Map<KVMObject, Long> addr;
	static Map<Long,KVMObject> inv;
	static long count=0L;
	static
	{
		addr=new HashMap<KVMObject, Long>();
		inv=new HashMap<Long, KVMObject>();
		count=0;
	}
	/**
	 * Every subclass of {@link ClassCode} should call this method to register itself.
	 * @param classID
	 * @param data
	 * @param flags
	 * @return
	 */
	public static long create(int classID,byte[] data,int flags)
	{
		return create(new KVMObject(classID, data, flags));
	}
	private static long create(KVMObject raw)
	{
		if(addr.containsKey(raw))
		{
			throw new RuntimeException();
		}
		addr.put(raw, count);
		inv.put(count, raw);
		return count++;
	}
	public static KVMObject retrieve(long val)
	{
		return inv.get(val);
	}
	public static long toAddress(KVMObject obj)
	{
		if(addr.containsKey(obj))
		{
			return addr.get(obj);
		}
		throw new IllegalArgumentException();
	}
	/**
	 * Address that indicates null reference.
	 */
	public static final long NULL_ADDR=-1;
	public static boolean isNull(long addr){
		return addr==NULL_ADDR;
	}
}
