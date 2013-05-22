package koba_app.klow;

public class Token1 //Struct
{
	public static final int // enum
		UNDEF=-1,
		OPER=101,
		IDENT=201,
		NUMBER=301,
		PAREN_B=401,//'('
		PAREN_E=451,//')'
		B_PAREN_B=501,//B_PAREN_B:'['
		B_PAREN_E=601;
	public String str;
	public int kind;
	public Token1(String s)
	{
		str=s;
		kind=UNDEF;
	}
	public void judge()
	{
		//kind=???;
		if(isOper())
		{kind=OPER;return;}
		int paren=isParen();
		if(paren>=0){kind=paren;return;}
		if(isIdent()){kind=IDENT;return;}
		if(isNum()){kind=NUMBER;return;}
		kind=UNDEF;return;
	}
	private boolean isOper()
	{
		final String[] opers={
		"+","-","*","/","%","+=","-=","*=","/=","%=",
		"<",">","<=",">=","==","!=",
		"<<",">>",">>>","<<=",">>=",">>>=",
		"^","~","&","|","^=","~=","&=","|=",
		"!","&&","||","?",":",".",
		"{","}"};
		for(String s:opers)
		{
			if(str.equals(s))
				return true;
		}
		return false;
	}
	private int isParen()
	{
		if(str.equals("["))return B_PAREN_B;
		if(str.equals("]"))return B_PAREN_E;
		if(str.equals("("))return PAREN_B;
		if(str.equals(")"))return PAREN_E;
		return -1;
	}
	private boolean isIdent(){
		char f=str.charAt(0);
		return ('A'<=f&&f<='Z')||('a'<=f&&f<='z')||f=='_';

	}
	private boolean isNum()
	{
		char f=str.charAt(0);
		return '0'<=f&&f<='9';
	}
}
