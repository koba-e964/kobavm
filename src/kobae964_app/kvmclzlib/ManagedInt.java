package kobae964_app.kvmclzlib;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ManagedInt{
	private int val;
	private ManagedInt(int raw)
	{
		val=raw;
	}
	/**
	 * Instantiates a ManagedInt. 
	 * @param raw an integer that returned instance holds
	 * @return the instance
	 */
	public static ManagedInt valueOf(int raw)
	{
		if(memo.containsKey(raw))
		{
			memo.put(raw,memo.get(raw)+1L);
		}
		else
		{
			memo.put(raw, 1L);
		}
		return new ManagedInt(raw);
	}
	public ManagedInt add(ManagedInt another)
	{
		return valueOf(val+another.val);
	}
	public ManagedInt sub(ManagedInt another)
	{
		return valueOf(val-another.val);
	}
	public ManagedInt mul(ManagedInt another)
	{
		return valueOf(val*another.val);
	}
	public ManagedInt div(ManagedInt another)
	{
		return valueOf(val/another.val);
	}
	public boolean equals(ManagedInt b)
	{
		return val==b.val;
	}
	public boolean lt(ManagedInt b)
	{
		return val<b.val;
	}
	public boolean le(ManagedInt b)
	{
		return val<=b.val;
	}
	private static Map<Integer, Long> memo=new TreeMap<Integer, Long>();
	public static String getStatistics()
	{
		return memo.toString();
	}
}
