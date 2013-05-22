package koba_app.simpl;


import static koba_app.simpl.CalcScanner.TokenType.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	CalcScanner scan;
	VarId vi;
	public Parser(String[] split,VarId vi)
	{
		this.scan=new CalcScanner(split);
		this.vi=vi;
	}
	public Parser(CalcScanner scan,VarId vi)
	{
		this.scan=scan;
		this.vi=vi;
	}
	
	public NumericExpression numeric()
	{
		return new NumericExpression(scan.next(),vi);
	}
	public static abstract class AbstractExpression
	{
		public abstract byte[] toMachine();
		public abstract int varId();
		public abstract String asm(int addr);
	}
	public static class NumericExpression extends AbstractExpression
	{
		Token num;
		int id;
		public NumericExpression(Token tok,VarId vi)
		{
			if(tok.getType()!=NUMERIC)
			{
				throw new IllegalArgumentException();
			}
			num=tok;
			id=vi.nextInt();
		}
		public int getValue()
		{
			return Integer.parseInt(num.toString());
		}
		@Override
		public byte[] toMachine()
		{
			byte[] out=new byte[]{0x10,0x42,0x07,0,0,0,0,0,0,0,0};
			System.arraycopy(Util.intToBytes(-id*4),0,out,3,4);
			System.arraycopy(Util.intToBytes(getValue()),0,out,7,4);
			return out;
		}
		@Override
		public int varId() {
			return id;
		}
		@Override
		public String asm(int addr)
		{
			return String.format("%08x:mov [rs5-%d],0x%x\t10 42 07 *8bytes*\n",addr,id*4,getValue());
		}
	}
	/**
	 * UnaryExpression = + UnaryExpression | - UnaryExpression | (Expression) | NumericExpression
	 *
	 */
	public UnaryExpression unary()
	{
		Token next=scan.peek();
		if(next.getType()==OPERATOR)
		{
			next=scan.next();
			return new UnaryExpression(next,unary(),vi);
		}
		if(next.getType()==LEFT_PAREN)
		{
			next=scan.next();
			UnaryExpression una=new UnaryExpression(exp(),vi);
			if(scan.next().getType()!=RIGHT_PAREN)
				throw new IllegalArgumentException();
			return una;
		}
		return new UnaryExpression(numeric(),vi);
	}
	public static class UnaryExpression extends AbstractExpression
	{
		NumericExpression num;
		Token op=null;
		UnaryExpression uex=null;// '-' . UnaryExpression
		Expression ex=null;//'(' Expression ')'
		int id;
		public UnaryExpression(Token op,UnaryExpression uex,VarId vi)
		{
			if(op.getType()!=OPERATOR)
			{
				throw new IllegalArgumentException();
			}
			this.op=op;
			this.uex=uex;
			id=vi.nextInt();
		}
		public UnaryExpression(NumericExpression nex,VarId vi)
		{
			num=nex;
			//doesn't allocate new varid
		}
		public UnaryExpression(Expression ex,VarId vi)
		{
			this.ex=ex;
			vi.nextInt();
		}
		public int getValue()
		{
			if(num==null)
			{
				if(ex!=null)
					return ex.getValue();
				return uex.getValue()* (op.toString().equals("-")?-1:1);
			}
			return num.getValue();
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
			byte opc;
			int tid=this.varId();
			if(num==null)
			{
				if(ex!=null)
				{//(exp)
					return ex.toMachine();
				}
				add(out,uex.toMachine());
				//TODO +/-
				//TODO add neg opcode or nothing
			}
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
		public int varId() {
			return id;
		}
		@Override
		public String asm(int addr) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	/**
	 *  MultiplicativeExpression = UnaryExpression ((*|/) UnaryExpression)
	 */
	public MultiplicativeExpression multi()
	{
		MultiplicativeExpression left=new MultiplicativeExpression(unary());
		while(true)
		{
			Token next=scan.peek();
			if(next == null || next.getType()!=OPERATOR_M)
			{break;}
			next=scan.next();
			UnaryExpression right=unary();
			left=new MultiplicativeExpression(left, right,next);
		}
		return left;
	}
	public static class MultiplicativeExpression extends AbstractExpression
	{
		MultiplicativeExpression left;
		UnaryExpression right;
		boolean div=false;
		public MultiplicativeExpression(UnaryExpression un)
		{
			this.left=null;
			this.right=un;
		}

		public MultiplicativeExpression(MultiplicativeExpression left,UnaryExpression right,Token tok)
		{
			this.left=left;
			this.right=right;
			this.div=tok.toString().equals("/");
		}
		public int getValue()
		{
			if(left==null)
				return right.getValue();
			int l=left.getValue();
			int r=right.getValue();
			return div?l/r:l*r;
		}
	}
	/**
	 * AdditiveExpression = MultiplicativeExpression ((+|-) MulplicativeExpression)*
	 */
	public AdditiveExpression additive()
	{
		AdditiveExpression left=new AdditiveExpression(multi());
		while(true)
		{
			Token next=scan.peek();
			if(next == null || next.getType()!=OPERATOR)
			{break;}
			next=scan.next();
			MultiplicativeExpression right=multi();
			left=new AdditiveExpression(left, right,next);
		}
		return left;
	}
	public static class AdditiveExpression extends AbstractExpression
	{
		AdditiveExpression left;
		MultiplicativeExpression right;
		boolean subt=false;
		public AdditiveExpression(MultiplicativeExpression un)
		{
			this.left=null;
			this.right=un;
		}

		public AdditiveExpression(AdditiveExpression left,MultiplicativeExpression right,Token tok)
		{
			this.left=left;
			this.right=right;
			this.subt=tok.toString().equals("-");
		}
		public int getValue()
		{
			if(left==null)
				return right.getValue();
			int l=left.getValue();
			int r=right.getValue();
			return subt?l-r:l+r;
		}
	}
	public Expression exp()
	{
		return new Expression(additive());
	}
	public static class Expression extends AbstractExpression
	{
		AdditiveExpression ar;
		public Expression(AdditiveExpression ae)
		{
			this.ar=ae;
		}
		public int getValue()
		{
			return ar.getValue();
		}
	}
}
