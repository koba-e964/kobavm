package koba_app.klow;

import java.util.*;

class Tokenizer
{
	private static final int
			ALPHA_NUM=1,
			OPER=2,
			PAREN=3,
			SPACE=4;
	private static boolean isSpace(char a)
	{
		return a>=0&&a<=0x20;
	}
	private static boolean isAlp(char a)
	{
		return (a>='A'&&a<='Z')||(a>='a'&&a<='z');
	}
	private static boolean isNum(char a)
	{
		return a>='0'&&a<='9';
	}
	private static boolean isAlphaNum(char a)
	{
		return isAlp(a)||isNum(a);
	}
	private static boolean isAscii(char a)
	{
		return a>=0&&a<0x80;
	}
	private static boolean isOperator(char a)
	{
		switch(a)
		{
			case '%':
			case '+':
			case '-':
			case '*':
			case '/':
			case '<':
			case '>':
			case '=':
			case '|':
			case '&':
			case '!':
			case '.':
				return true;
			default:
		}
		return false;
	}
	private static boolean isParen(char a)
	{
		switch(a)
		{
			case'[':
			case']':
			case'(':
			case')':
			case':':
				return true;
			default:
		}
		return false;
	}
	private static int getType(char a)
	{
		if(isAlphaNum(a))	return ALPHA_NUM;
		if(isOperator(a))	return OPER;
		if(isParen(a))		return PAREN;
		if(isSpace(a))		return SPACE;
					return 0;
	}
	public static List<String> split(String s)
	{
		List<String> out=new ArrayList<String>(0x100);
		{
			int cLen=s.length();
			int curType=0;
			StringBuilder sb=new StringBuilder(0x100);
			for(int i=0;i<cLen;i++)
			{
				int tmp=getType(s.charAt(i));
				if(tmp==PAREN||curType!=tmp)
				{
					if(curType!=SPACE)
					{
						out.add(sb.toString());
					}
					sb=new StringBuilder(0x100);
					curType=tmp;
				}
				sb.append(s.charAt(i));	
			}
			if(curType!=SPACE)
				out.add(sb.toString());
		}
		return out;
	}
	public static List<String> split_il(String s)
	{
		class Tmp
		{
			int getType(char c)
			{
				if(c<=' ')
					return SPACE;
				switch(c)
				{
				case '(':
				case ')':
					return PAREN;
				case ':':
					return OPER;
				default:
					return ALPHA_NUM;
				}
				
			}
		};
		List<String> out=new ArrayList<String>(0x100);
		{
			int cLen=s.length();
			int curType=0;
			StringBuilder sb=new StringBuilder(0x100);
			for(int i=0;i<cLen;i++)
			{
				int tmp=new Tmp().getType(s.charAt(i));
				if(tmp==PAREN||curType!=tmp)
				{
					if(curType!=SPACE)
						out.add(sb.toString());
					sb=new StringBuilder(0x100);
					curType=tmp;
				}
				sb.append(s.charAt(i));	
			}
			if(curType!=SPACE)
				out.add(sb.toString());
		}
		return out;
	}
	public static void main(String[] args)
	{
		List<String> list=split("1*2+3*(0x4f-rev);");
		for(String s :list)
		{
			System.out.println(s);
		}
	}
}