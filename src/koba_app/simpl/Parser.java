package koba_app.simpl;


import static koba_app.simpl.CalcScanner.TokenType.*;

public class Parser {
	CalcScanner scan;
	public Parser(String[] split)
	{
		this.scan=new CalcScanner(split);
	}
	public Parser(CalcScanner scan)
	{
		this.scan=scan;
	}
	
	public NumericExpression numeric()
	{
		return new NumericExpression(scan.next());
	}
	public static class NumericExpression
	{
		Token num;
		public NumericExpression(Token tok)
		{
			if(tok.getType()!=NUMERIC)
			{
				throw new IllegalArgumentException();
			}
			num=tok;
		}
		public int getValue()
		{
			return Integer.parseInt(num.toString());
		}
	}
	/**
	 * UnaryExpression = + UnaryExpression | - UnaryExpression | (Expression) | NumericExpression
	 *
	 */
	public UnaryExpression unary()
	{
		Token next=scan.peek();
		if(next.type==OPERATOR)
		{
			next=scan.next();
			return new UnaryExpression(next,unary());
		}
		if(next.getType()==LEFT_PAREN)
		{
			next=scan.next();
			UnaryExpression una=new UnaryExpression(exp());
			if(scan.next().getType()!=RIGHT_PAREN)
				throw new IllegalArgumentException();
			return una;
		}
		return new UnaryExpression(numeric());
	}
	public static class UnaryExpression
	{
		NumericExpression num;
		Token op=null;
		UnaryExpression uex=null;
		Expression ex=null;
		public UnaryExpression(Token op,UnaryExpression uex)
		{
			if(op.getType()!=OPERATOR)
			{
				throw new IllegalArgumentException();
			}
			this.op=op;
			this.uex=uex;
		}
		public UnaryExpression(NumericExpression nex)
		{
			num=nex;
		}
		public UnaryExpression(Expression ex)
		{
			this.ex=ex;
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
	public static class MultiplicativeExpression
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
	public static class AdditiveExpression
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
	public static class Expression
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
