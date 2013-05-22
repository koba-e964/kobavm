package koba_app.simpl;


public class Leaf extends BasicTree
{
	private int vn=-1;
	private String cont;
	private int intVal;
	public Leaf(String c,VarId vi)
	{
		this.cont=c;
		vn=vi.nextInt();
		intVal=Integer.parseInt(this.cont);
	}
	@Override
	public BasicTree[] children(){return null;}
	@Override
	public byte[] toMachine()
	{
		byte[] out=new byte[]{0x10,0x42,0x07,0,0,0,0,0,0,0,0};
		System.arraycopy(Util.intToBytes(-vn*4),0,out,3,4);
		System.arraycopy(Util.intToBytes(intVal),0,out,7,4);
		return out;
	}
	@Override
	public String asm(int addr)
	{
		return String.format("%08x:mov [rs5-%d],0x%x\t10 42 07 *8bytes*\n",addr,vn*4,intVal);
	}
	@Override
	public int varId(){return vn;}
	@Override
	public String toString()
	{
		return cont;
	}
}