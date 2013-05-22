package koba_app.simpl;
import java.util.*;
public class Tree extends BasicTree
{
	private BasicTree[] bts;
	private char op;
	public Tree(char op,BasicTree[] bts,VarId vi)
	{
		this.op=op;
		this.bts=bts.clone();
		vn=vi.nextInt();
	}
	private static byte[] intToBytes(int val)
	{
		byte[] out=new byte[4];
		for(int i=0;i<4;i++)
		{
			out[i]=(byte)val;
			val>>>=8;
		}
		return out;
	}
	private static String intToBytesStr(int val)
	{
		final char[] format="0123456789abcdef".toCharArray();
		char[] out=new char[3*4-1];
		for(int i=0;i<4;i++)
		{
			out[3*i+1]=format[val&0x0f];
			out[3*i]=format[(val>>4)&0xf];
			if(i<=2)out[3*i+2]=' ';
			val>>>=8;
		}
		return new String(out);
	}
	@Override
	public BasicTree[] children()
	{
		return bts.clone();
	}
	private static void add(List<Byte> list,byte[] ary)
	{
		for(byte b:ary)
			list.add(b);
	}
	@Override
	public byte[] toMachine()
	{
		List<Byte> out=new ArrayList<Byte>(0x400);
		for(BasicTree bt:bts)
		{
			add(out,bt.toMachine());
		}
		byte opc;
		int a1=bts[0].varId();
		int a2=bts[1].varId();
		int tid=this.varId();
		switch(this.op)
		{
		case '+':
			opc=0x11;
			break;
		case '-':
			opc=0x13;
			break;
		case '*':
			opc=0x18;
			break;
		case '/':
			opc=0x20;break;
		default:opc=(byte)0xff;
		}
		add(out,new byte[]{0x10,0x20,0x70});//mov ra5,[rs5-??]
		add(out,intToBytes(-a1*4));
		add(out,new byte[]{0x10,0x20,0x73});//mov rd5,[rs5-??]
		add(out,intToBytes(-a2*4));
		if(this.op!='/' && this.op !='%')
		{
			add(out,new byte[]{opc,0x00,0x30});	//OP ra5,rd5
		}
		else
		{
			out.add((byte)0x20);
		}
		add(out,new byte[]{0x10,0x02,0x07});//mov [rs5-??],ra5
		add(out,intToBytes(-tid*4));
		byte[] outary=new byte[out.size()];
		for(int i=0;i<outary.length;i++)
		{outary[i]=out.get(i);}
		return outary;
	}
	@Override
	public String asm(int addr)
	{
		StringBuilder sb=new StringBuilder(0x100);
		for(BasicTree bt:bts)
		{
			sb.append(bt.asm(addr));
			addr+=bt.toMachine().length;
		}
		byte opc;
		String opstr;
		int a1=bts[0].varId();
		int a2=bts[1].varId();
		int tid=this.varId();
		switch(this.op)
		{
		case '+':
			opc=0x11;
			opstr="add";
			break;
		case '-':
			opc=0x13;
			opstr="sub";
			break;
		case '*':
			opc=0x18;
			opstr="mul";
			break;
		case '/':
			opc=0x20;opstr="div";break;
		default:opc=(byte)0xff;opstr="syscall";
		}
		sb.append(String.format("%08x:mov ra5,[rs5-%d]\t10 20 70 ",addr,a1*4));//
		sb.append(intToBytesStr(-a1*4)).append("\n");
		addr+=7;
		sb.append(String.format("%08x:mov rd5,[rs5-%d]\t10 20 73 ",addr,a2*4));//mov rd5,[rs5-??]
		sb.append(intToBytesStr(-a2*4)).append("\n");
		addr+=7;
		if(this.op!='/' && this.op !='%')
		{
			sb.append(String.format("%08x:",addr)+opstr+" ra5,rd5\t");
			sb.append(String.format("%02x 00 30\n",opc&0xff));//OP ra5,rd5
			addr+=3;
		}
		else
		{
			sb.append(String.format("%08x:div ra5,rd5\t20\n",addr));
			addr+=1;
		}
		sb.append(String.format("%08x:mov [rs5-%d],ra5\t10 02 07 ",addr,tid*4));//mov [rs5-??],ra5
		sb.append(intToBytesStr(-tid*4)).append("\n");
		return sb.toString();
	}
	@Override
	public int varId()
	{
		return vn;
	}
	@Override
	public String toString()
	{
		StringBuilder sb= new StringBuilder(100).append("( ").append(op)
				.append(' ');
		for(BasicTree bt : bts)
		{
			sb.append(bt.toString());
			sb.append(' ');
		}
		sb.append(')');
		return sb.toString();
	}
	private int vn=-1;
}
