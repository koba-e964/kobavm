package kobae964_app.kvm3;

import java.util.HashMap;
import java.util.Map;

public class Heap {
	Map<KVMObject, Long> addr;
	Map<Long,KVMObject> inv;
	long count=0L;
	public Heap()
	{
		addr=new HashMap<KVMObject, Long>();
		inv=new HashMap<Long, KVMObject>();
		count=0;
	}
	long create(KVMObject raw)
	{
		if(addr.containsKey(raw))
		{
			throw new RuntimeException();
		}
		addr.put(raw, count);
		inv.put(count, raw);
		return count++;
	}
	KVMObject retrieve(long val)
	{
		return inv.get(val);
	}

}
