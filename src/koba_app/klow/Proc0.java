/**
	Proc0.java
	
	String -> Token1(which has info of 
			token(ex. operator,identifier,numeral,...)
*/

package koba_app.klow;

import java.util.*;

public class Proc0
{
	private List<Token1> tokens;
	public Proc0(List<String> toks)
	{
		tokens=new ArrayList<Token1>(toks.size());
		for(String s : toks)
		{
			Token1 tok1=new Token1(s);
			tok1.judge();
			tokens.add(tok1);
		}
	}
	public List<Token1> conv()
	{
		return new ArrayList<Token1>(tokens);
	}
}
