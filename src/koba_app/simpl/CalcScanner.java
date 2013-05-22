package koba_app.simpl;

public class CalcScanner {
	public CalcScanner(String[] split)
	{
		token=new Token[split.length];
		for(int i=0;i<split.length;i++)
		{
			token[i]=new Token(split[i]);
		}
	}
	Token[] token;
	int pos=0;
	public Token next()
	{
		if(pos<token.length)
			return token[pos++];
		return null;
	}
	public Token peek()
	{
		if(pos<token.length)
			return token[pos];
		return null;
	}
	public static enum TokenType
	{
		NUMERIC,
		OPERATOR,
		OPERATOR_M,
		LEFT_PAREN,
		RIGHT_PAREN,
	}
}
