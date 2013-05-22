package koba_app.simpl;


import java.io.*;
import java.util.*;
import koba_app.compiler.*;
import koba_app.simpl.Parser.Expression;


public class Simpl
{
	private static class StackInt
	{
		private int[] dat;
		private int pl=0;
		StackInt(int max)
		{
			this.dat=new int[max];
		}
		public void push(int val)throws IndexOutOfBoundsException
		{
			dat[pl]=val;
			pl++;
		}
		public int pop()throws IndexOutOfBoundsException
		{
			pl--;
			return dat[pl];
		}
		public int peek(int ind)throws IndexOutOfBoundsException
		{
			if(ind<=0)throw new IndexOutOfBoundsException();
			return dat[pl-ind];
		}
		public int position(){return pl;}
		@Override
		public String toString()
		{
			StringBuilder sb=new StringBuilder(100);
			sb.append("(Simpl.StackInt");
			for(int val:this.dat)
			{
				sb.append('(');
				sb.append(val);
			}
			return sb.toString();
		}
	}
	static int prio(int op)
	{
		switch(op)
		{
		case '+':case '-':return 1;
		case '*':case '/':return 2;
		default:return 0;
		}
	}
	public static void destruct(Deque<BasicTree> vals,StackInt ops,int minPrio,VarId vi)
	{//{the priority of ops[i]} is an increasing sequence.
		while(ops.position()>=1 && prio(ops.peek(1))>=minPrio)
		{
			BasicTree oper2=vals.pollLast();
			BasicTree oper1=vals.pollLast();
			Tree tr=new Tree((char)ops.pop(),new BasicTree[]{oper1,oper2,},vi);
			vals.addLast(tr);
		}
	}
	public static void main(String[] args)throws IOException
	{
		Scanner sc=new Scanner(System.in);
		VarId vi=new VarId();

		String line=sc.nextLine().trim();
		String[] toks=Tokenizer.split(line);
		System.out.print("(input ");
		for(String t:toks)
		{
			System.out.print(t+' ');
		}
		System.out.print(")");
		Parser ps=new Parser(toks,vi);
		Expression exp=ps.exp();
		String binout=args.length>=1?args[0]:"tcalc.bin";
		String asmout=args.length>=2?args[1]:"tcalc.asm";
		//byte[] mac=vals.getFirst().toMachine();
		RandomAccessFile ra=new RandomAccessFile(binout,"rw");
		ra.write(new byte[]{0x10,0x40,0x07,0x00,(byte)0x7c,0x00,0x00});
		//ra.write(mac);
		ra.write((byte)0xff);
		ra.close();
		//String asmcode=vals.getFirst().asm(0x07);
		FileWriter afw=new FileWriter(asmout);
		afw.write("00000000:mov rs5,0x7c00\t10 40 07 00 7c 00 00\n");
		//afw.write(asmcode);
		afw.write("exit\tff\n");
		afw.close();
	}
}
