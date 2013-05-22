package koba_app.compiler;

import java.util.*;
import static koba_app.compiler.Consts.*;
public class Tokenizer
{
	private Tokenizer()
	{
	}
	public static void main(String[] args)
	{
		String[] test1=split("");
		String[] test2=split("23+(25  *  44)");
		String[] test3=split_il("$temp1:$tmpfunc(87,10)");
		class Tmp
		{
			public void show(String[] s)
			{
				for(String t:s)
					System.out.print(t+"\r\n");
				System.out.print("\r\n");
			}
		};
		new Tmp().show(test1);
		new Tmp().show(test2);
		new Tmp().show(test3);
	}
	public static int getType(String s)
	{
		if(s.length()==0)
			return 0;
		return getType(s.charAt(0));
	}
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
	private static boolean isVal(char a)
	{
		return isAlp(a)||isNum(a);
	}
	@SuppressWarnings("unused")
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
	private static boolean isGram(char a)
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
		if(isVal(a))		return VAL;
		if(isOperator(a))	return OPER;
		if(isGram(a))		return GRAM;
		if(isSpace(a))		return SPACE;
					return 0;
	}
	public static String[] split(String s)
	{
		List<String> out=new ArrayList<String>(0x100);
		{
			int cLen=s.length();
			int curType=SPACE;
			StringBuilder sb=new StringBuilder(0x100);
			for(int i=0;i<cLen;i++)
			{
				int tmp=getType(s.charAt(i));
				if(tmp==GRAM||curType!=tmp)
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
		return out.toArray(new String[0]);
	}
	public static String[] split_il(String s)
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
					return GRAM;
				case ':':
					return OPER;
				default:
					return VAL;
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
				if(tmp==GRAM||curType!=tmp)
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
		return out.toArray(new String[0]);
	}
}