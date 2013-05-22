package koba_app.kvm;

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
			set_byte(addr+k,ba[k]);
		}
	}
	public byte get_byte(int addr)
	{
		if(addr<0||addr>m_size-1)
			return 0;
		return memory[addr];
	}
	public void set_byte(int addr,byte v)
	{
		if(addr<0||addr>=m_size-1)
			return;
		memory[addr]=(byte)v;
	}
	public void set_word(int addr,int v)
	{
		if(addr<0||addr>m_size-2)
			return;
		memory[addr]=(byte)v;
		memory[addr+1]=(byte)(v>>8);
	}
	public int get_word(int addr)
	{
		if(addr<0||addr>m_size-2)
			return 0;
		return 
		(memory[addr]&0xff)|
		(memory[addr+1]&0xff)<<8;
	}
	public void set_dword(int addr,int v)
	{
		if(addr<0||addr>m_size-4)
			return;
		memory[addr]=(byte)v;
		memory[addr+1]=(byte)(v>>8);
		memory[addr+2]=(byte)(v>>16);
		memory[addr+3]=(byte)(v>>24);
	}
	public int get_dword(int addr)
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
}