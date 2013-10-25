package kobae964_app.kvm3;

import java.util.HashMap;
import java.util.Map;

public class Heap {
	static Map<KVMObject, Long> addr;
	static Map<Long,KVMObject> inv;
	static Map<Long,Integer> refc;//reference count
	static long count=0L;
	static
	{
		addr=new HashMap<KVMObject, Long>();
		inv=new HashMap<Long, KVMObject>();
		refc=new HashMap<Long,Integer>();
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
		refc.put(count, 0);
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
	 * increments reference counter of addr.
	 * @param addr the address of target object.
	 */
	static void refer(long addr){
		if(!inv.containsKey(addr)){
			throw new IllegalArgumentException("not a valid address:"+addr);
		}
		refc.put(addr, refc.get(addr)+1);
	}
	/**
	 * decrements reference counter of addr.
	 * @param addr the address of target object.
	 */
	static void unrefer(long addr){
		if(!inv.containsKey(addr)){
			throw new IllegalArgumentException("not a valid address:"+addr);
		}
		refc.put(addr, refc.get(addr)-1);
		if(refc.get(addr)<=0){
			//gc
			System.out.println("object (addr="+addr+") became alone. gc...");
			//TODO garbage collection
			KVMObject obj=inv.get(addr);
			inv.remove(addr);
			Heap.addr.remove(obj);
			refc.remove(addr);
		}
	}
	static void dumpAll(){
		for(Map.Entry<Long, KVMObject> entry:inv.entrySet()){
			System.out.println("["+entry.getKey()+"]=>"+entry.getValue());
			System.out.println("refcount="+refc.get(entry.getKey()));
		}
	}
	/**
	 * Address that indicates null reference.
	 */
	public static final long NULL_ADDR=-1;
	public static boolean isNull(long addr){
		return addr==NULL_ADDR;
	}
}
