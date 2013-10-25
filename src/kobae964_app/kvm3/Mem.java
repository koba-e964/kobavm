package kobae964_app.kvm3;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Memory and its manager.
 * @author koba-e964
 *
 */
public class Mem
{
	public byte[] memory=null;
	int m_size=0;
	public Mem(int len)
	{
		if(len<0)
			throw new InternalError();
		memory=new byte[len];
		m_size=len;
	}
	public void load(byte[] ba,int addr)
	{
		for(int k=0;k<ba.length;k++)
		{
			setByte(addr+k,ba[k]);
		}
	}
	public byte getByte(int addr)
	{
		if(addr<0||addr>m_size-1)
			return 0;
		return memory[addr];
	}
	public void setByte(int addr,byte v)
	{
		if(addr<0||addr>=m_size-1)
			return;
		memory[addr]=(byte)v;
	}
	public void setWord(int addr,int v)
	{
		if(addr<0||addr>m_size-2)
			return;
		memory[addr]=(byte)v;
		memory[addr+1]=(byte)(v>>8);
	}
	public int getWord(int addr)
	{
		if(addr<0||addr>m_size-2)
			return 0;
		return 
		(memory[addr]&0xff)|
		(memory[addr+1]&0xff)<<8;
	}
	public void setDword(int addr,int v)
	{
		if(addr<0||addr>m_size-4)
			return;
		memory[addr]=(byte)v;
		memory[addr+1]=(byte)(v>>8);
		memory[addr+2]=(byte)(v>>16);
		memory[addr+3]=(byte)(v>>24);
	}
	public int getDword(int addr)
	{
		if(addr<0||addr>m_size-4)
			return 0;
		return 
		(memory[addr]&0xff)|
		(memory[addr+1]&0xff)<<8|
		(memory[addr+2]&0xff)<<16|
		(memory[addr+3]&0xff)<<24;
	}
	public String toString()
	{
		return "memory size:0x"+Integer.toHexString(m_size)+"\r\n";
	}
	//very naive manager.
	private int used=0;
	private Map<Integer,Integer> map=new HashMap<Integer, Integer>();//starting address->length
	public int allocate(int len){
		if(len<0){
			throw new IllegalArgumentException("length negative:"+len);
		}
		int tmp=used;
		used+=(len+3)&-4;
		if(used>m_size){
			throw new RuntimeException("memory ran out");
		}
		map.put(tmp,len);
		return tmp;
	}
	public void free(int addr){
		if(map.containsKey(addr)){
			map.remove(addr);
		}
	}
	public int getLength(int addr){
		return map.get(addr);
	}
	public Map<Integer,Integer> memView(){
		return Collections.unmodifiableMap(map);
	}
}