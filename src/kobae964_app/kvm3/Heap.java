package kobae964_app.kvm3;

import java.util.HashMap;
import java.util.Map;

public class Heap {
	Map<Object, Long> addr;
	Map<Long,Object> inv;
	long count=0L;
	public Heap()
	{
		addr=new HashMap<Object, Long>();
		inv=new HashMap<Long, Object>();
		count=0;
	}
	long create(Object raw)
	{
		if(addr.containsKey(raw))
		{
			throw new RuntimeException();
		}
		addr.put(raw, count);
		inv.put(count, raw);
		return count++;
	}
	Object retrieve(long val)
	{
		return inv.get(val);
	}

}
